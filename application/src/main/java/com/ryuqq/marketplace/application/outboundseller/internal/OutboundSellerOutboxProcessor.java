package com.ryuqq.marketplace.application.outboundseller.internal;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxReadManager;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundRefundPolicySyncClient;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundSellerAddressSyncClient;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundSellerSyncClient;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundShippingPolicySyncClient;
import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
public class OutboundSellerOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboundSellerOutboxProcessor.class);

    private final OutboundSellerOutboxCommandManager commandManager;
    private final OutboundSellerOutboxReadManager readManager;
    private final OutboundSellerCompletionFacade completionFacade;
    private final OutboundSellerSyncClient sellerSyncClient;
    private final OutboundShippingPolicySyncClient shippingPolicySyncClient;
    private final OutboundRefundPolicySyncClient refundPolicySyncClient;
    private final OutboundSellerAddressSyncClient sellerAddressSyncClient;
    private final SellerSalesChannelReadManager salesChannelReadManager;
    private final ShopReadManager shopReadManager;
    private final Clock clock;

    public OutboundSellerOutboxProcessor(
            OutboundSellerOutboxCommandManager commandManager,
            OutboundSellerOutboxReadManager readManager,
            OutboundSellerCompletionFacade completionFacade,
            OutboundSellerSyncClient sellerSyncClient,
            OutboundShippingPolicySyncClient shippingPolicySyncClient,
            OutboundRefundPolicySyncClient refundPolicySyncClient,
            OutboundSellerAddressSyncClient sellerAddressSyncClient,
            SellerSalesChannelReadManager salesChannelReadManager,
            ShopReadManager shopReadManager,
            Clock clock) {
        this.commandManager = commandManager;
        this.readManager = readManager;
        this.completionFacade = completionFacade;
        this.sellerSyncClient = sellerSyncClient;
        this.shippingPolicySyncClient = shippingPolicySyncClient;
        this.refundPolicySyncClient = refundPolicySyncClient;
        this.sellerAddressSyncClient = sellerAddressSyncClient;
        this.salesChannelReadManager = salesChannelReadManager;
        this.shopReadManager = shopReadManager;
        this.clock = clock;
    }

    public void processOutbox(OutboundSellerOutbox outbox) {
        Instant now = clock.instant();

        outbox.startProcessing(now);
        commandManager.persist(outbox);

        try {
            OutboundSellerSyncResult result = dispatch(outbox);

            if (result.success()) {
                completionFacade.completeOutbox(outbox.idValue(), clock.instant());
            } else {
                persistFailureWithReRead(
                        outbox.idValue(), result.retryable(), result.errorMessage());
            }
        } catch (Exception e) {
            log.error(
                    "Outbound seller outbox 처리 중 예외 발생. outboxId={}, entityType={}, entityId={}",
                    outbox.idValue(),
                    outbox.entityType(),
                    outbox.entityId(),
                    e);
            persistFailureWithReRead(outbox.idValue(), true, e.getMessage());
        }
    }

    private OutboundSellerSyncResult dispatch(OutboundSellerOutbox outbox) {
        Shop shop = resolveShop(outbox.sellerIdValue());
        return switch (outbox.entityType()) {
            case SELLER -> dispatchSeller(shop, outbox);
            case SHIPPING_POLICY -> dispatchShippingPolicy(shop, outbox);
            case REFUND_POLICY -> dispatchRefundPolicy(shop, outbox);
            case SELLER_ADDRESS -> dispatchSellerAddress(shop, outbox);
        };
    }

    private Shop resolveShop(Long sellerId) {
        List<SellerSalesChannel> channels =
                salesChannelReadManager.findConnectedBySellerId(SellerId.of(sellerId));
        if (channels.isEmpty()) {
            throw new IllegalStateException(
                    "셀러에 연결된 판매채널이 없습니다. sellerId=" + sellerId);
        }
        long shopId = channels.get(0).shopId();
        return shopReadManager.getById(ShopId.of(shopId));
    }

    private OutboundSellerSyncResult dispatchSeller(Shop shop, OutboundSellerOutbox outbox) {
        return switch (outbox.operationType()) {
            case CREATE -> sellerSyncClient.createSeller(shop, outbox.sellerIdValue());
            case UPDATE -> sellerSyncClient.updateSeller(shop, outbox.sellerIdValue());
            case DELETE ->
                    OutboundSellerSyncResult.nonRetryableFailure(
                            "UNSUPPORTED", "셀러 삭제 동기화는 지원하지 않습니다");
        };
    }

    private OutboundSellerSyncResult dispatchShippingPolicy(Shop shop, OutboundSellerOutbox outbox) {
        return switch (outbox.operationType()) {
            case CREATE ->
                    shippingPolicySyncClient.createShippingPolicy(
                            shop, outbox.sellerIdValue(), outbox.entityId());
            case UPDATE ->
                    shippingPolicySyncClient.updateShippingPolicy(
                            shop, outbox.sellerIdValue(), outbox.entityId());
            case DELETE ->
                    OutboundSellerSyncResult.nonRetryableFailure(
                            "UNSUPPORTED", "배송정책 삭제 동기화는 지원하지 않습니다");
        };
    }

    private OutboundSellerSyncResult dispatchRefundPolicy(Shop shop, OutboundSellerOutbox outbox) {
        return switch (outbox.operationType()) {
            case CREATE ->
                    refundPolicySyncClient.createRefundPolicy(
                            shop, outbox.sellerIdValue(), outbox.entityId());
            case UPDATE ->
                    refundPolicySyncClient.updateRefundPolicy(
                            shop, outbox.sellerIdValue(), outbox.entityId());
            case DELETE ->
                    OutboundSellerSyncResult.nonRetryableFailure(
                            "UNSUPPORTED", "환불정책 삭제 동기화는 지원하지 않습니다");
        };
    }

    private OutboundSellerSyncResult dispatchSellerAddress(Shop shop, OutboundSellerOutbox outbox) {
        return switch (outbox.operationType()) {
            case CREATE ->
                    sellerAddressSyncClient.createSellerAddress(
                            shop, outbox.sellerIdValue(), outbox.entityId());
            case UPDATE ->
                    sellerAddressSyncClient.updateSellerAddress(
                            shop, outbox.sellerIdValue(), outbox.entityId());
            case DELETE ->
                    sellerAddressSyncClient.deleteSellerAddress(
                            shop, outbox.sellerIdValue(), outbox.entityId());
        };
    }

    private void persistFailureWithReRead(Long outboxId, boolean retryable, String errorMessage) {
        try {
            OutboundSellerOutbox fresh = readManager.getById(outboxId);
            fresh.recordFailure(retryable, errorMessage, clock.instant());
            commandManager.persist(fresh);
        } catch (Exception e) {
            log.error("Outbound seller outbox 실패 기록 중 오류. outboxId={}", outboxId, e);
        }
    }
}
