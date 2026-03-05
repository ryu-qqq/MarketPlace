package com.ryuqq.marketplace.application.setofsync.internal;

import com.ryuqq.marketplace.application.setofsync.dto.response.SetofSyncResult;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxReadManager;
import com.ryuqq.marketplace.application.setofsync.port.out.client.SetofRefundPolicySyncClient;
import com.ryuqq.marketplace.application.setofsync.port.out.client.SetofSellerAddressSyncClient;
import com.ryuqq.marketplace.application.setofsync.port.out.client.SetofSellerSyncClient;
import com.ryuqq.marketplace.application.setofsync.port.out.client.SetofShippingPolicySyncClient;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import java.time.Clock;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofSyncOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(SetofSyncOutboxProcessor.class);

    private final SetofSyncOutboxCommandManager commandManager;
    private final SetofSyncOutboxReadManager readManager;
    private final SetofSyncCompletionFacade completionFacade;
    private final SetofSellerSyncClient sellerSyncClient;
    private final SetofShippingPolicySyncClient shippingPolicySyncClient;
    private final SetofRefundPolicySyncClient refundPolicySyncClient;
    private final SetofSellerAddressSyncClient sellerAddressSyncClient;
    private final Clock clock;

    public SetofSyncOutboxProcessor(
            SetofSyncOutboxCommandManager commandManager,
            SetofSyncOutboxReadManager readManager,
            SetofSyncCompletionFacade completionFacade,
            SetofSellerSyncClient sellerSyncClient,
            SetofShippingPolicySyncClient shippingPolicySyncClient,
            SetofRefundPolicySyncClient refundPolicySyncClient,
            SetofSellerAddressSyncClient sellerAddressSyncClient,
            Clock clock) {
        this.commandManager = commandManager;
        this.readManager = readManager;
        this.completionFacade = completionFacade;
        this.sellerSyncClient = sellerSyncClient;
        this.shippingPolicySyncClient = shippingPolicySyncClient;
        this.refundPolicySyncClient = refundPolicySyncClient;
        this.sellerAddressSyncClient = sellerAddressSyncClient;
        this.clock = clock;
    }

    public void processOutbox(SetofSyncOutbox outbox) {
        Instant now = clock.instant();

        outbox.startProcessing(now);
        commandManager.persist(outbox);

        try {
            SetofSyncResult result = dispatch(outbox);

            if (result.success()) {
                completionFacade.completeOutbox(outbox.idValue(), clock.instant());
            } else {
                persistFailureWithReRead(
                        outbox.idValue(), result.retryable(), result.errorMessage());
            }
        } catch (Exception e) {
            log.error(
                    "Setof sync outbox 처리 중 예외 발생. outboxId={}, entityType={}, entityId={}",
                    outbox.idValue(),
                    outbox.entityType(),
                    outbox.entityId(),
                    e);
            persistFailureWithReRead(outbox.idValue(), true, e.getMessage());
        }
    }

    private SetofSyncResult dispatch(SetofSyncOutbox outbox) {
        return switch (outbox.entityType()) {
            case SELLER -> dispatchSeller(outbox);
            case SHIPPING_POLICY -> dispatchShippingPolicy(outbox);
            case REFUND_POLICY -> dispatchRefundPolicy(outbox);
            case SELLER_ADDRESS -> dispatchSellerAddress(outbox);
        };
    }

    private SetofSyncResult dispatchSeller(SetofSyncOutbox outbox) {
        return switch (outbox.operationType()) {
            case CREATE -> sellerSyncClient.createSeller(outbox.sellerIdValue());
            case UPDATE -> sellerSyncClient.updateSeller(outbox.sellerIdValue());
            case DELETE ->
                    SetofSyncResult.nonRetryableFailure("UNSUPPORTED", "셀러 삭제 동기화는 지원하지 않습니다");
        };
    }

    private SetofSyncResult dispatchShippingPolicy(SetofSyncOutbox outbox) {
        return switch (outbox.operationType()) {
            case CREATE ->
                    shippingPolicySyncClient.createShippingPolicy(
                            outbox.sellerIdValue(), outbox.entityId());
            case UPDATE ->
                    shippingPolicySyncClient.updateShippingPolicy(
                            outbox.sellerIdValue(), outbox.entityId());
            case DELETE ->
                    SetofSyncResult.nonRetryableFailure("UNSUPPORTED", "배송정책 삭제 동기화는 지원하지 않습니다");
        };
    }

    private SetofSyncResult dispatchRefundPolicy(SetofSyncOutbox outbox) {
        return switch (outbox.operationType()) {
            case CREATE ->
                    refundPolicySyncClient.createRefundPolicy(
                            outbox.sellerIdValue(), outbox.entityId());
            case UPDATE ->
                    refundPolicySyncClient.updateRefundPolicy(
                            outbox.sellerIdValue(), outbox.entityId());
            case DELETE ->
                    SetofSyncResult.nonRetryableFailure("UNSUPPORTED", "환불정책 삭제 동기화는 지원하지 않습니다");
        };
    }

    private SetofSyncResult dispatchSellerAddress(SetofSyncOutbox outbox) {
        return switch (outbox.operationType()) {
            case CREATE ->
                    sellerAddressSyncClient.createSellerAddress(
                            outbox.sellerIdValue(), outbox.entityId());
            case UPDATE ->
                    sellerAddressSyncClient.updateSellerAddress(
                            outbox.sellerIdValue(), outbox.entityId());
            case DELETE ->
                    sellerAddressSyncClient.deleteSellerAddress(
                            outbox.sellerIdValue(), outbox.entityId());
        };
    }

    private void persistFailureWithReRead(Long outboxId, boolean retryable, String errorMessage) {
        try {
            SetofSyncOutbox fresh = readManager.getById(outboxId);
            fresh.recordFailure(retryable, errorMessage, clock.instant());
            commandManager.persist(fresh);
        } catch (Exception e) {
            log.error("Setof sync outbox 실패 기록 중 오류. outboxId={}", outboxId, e);
        }
    }
}
