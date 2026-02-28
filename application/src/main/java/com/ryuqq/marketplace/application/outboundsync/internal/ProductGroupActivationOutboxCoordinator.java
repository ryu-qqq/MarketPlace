package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductCommandManager;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundsync.factory.OutboundSyncCommandFactory;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 상품그룹 활성화 시 OutboundSyncOutbox + OutboundProduct 생성 코디네이터.
 *
 * <p>PostAnalysisProductGroupCoordinator와 BatchChangeProductGroupStatusService에서 공통으로 사용합니다.
 *
 * <p>이미 PENDING 상태의 Outbox가 존재하는 채널은 중복 생성하지 않습니다.
 */
@Component
public class ProductGroupActivationOutboxCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(ProductGroupActivationOutboxCoordinator.class);

    private final SellerSalesChannelReadManager sellerSalesChannelReadManager;
    private final OutboundSyncCommandFactory outboundSyncCommandFactory;
    private final OutboundSyncOutboxCommandManager outboxCommandManager;
    private final OutboundSyncOutboxReadManager outboxReadManager;
    private final OutboundProductCommandManager outboundProductCommandManager;
    private final OutboundProductReadManager outboundProductReadManager;

    public ProductGroupActivationOutboxCoordinator(
            SellerSalesChannelReadManager sellerSalesChannelReadManager,
            OutboundSyncCommandFactory outboundSyncCommandFactory,
            OutboundSyncOutboxCommandManager outboxCommandManager,
            OutboundSyncOutboxReadManager outboxReadManager,
            OutboundProductCommandManager outboundProductCommandManager,
            OutboundProductReadManager outboundProductReadManager) {
        this.sellerSalesChannelReadManager = sellerSalesChannelReadManager;
        this.outboundSyncCommandFactory = outboundSyncCommandFactory;
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.outboundProductCommandManager = outboundProductCommandManager;
        this.outboundProductReadManager = outboundProductReadManager;
    }

    /**
     * CONNECTED 채널에 대한 OutboundSyncOutbox + OutboundProduct 생성.
     *
     * <p>이미 PENDING Outbox가 있는 채널은 Outbox 중복 생성을 건너뛰고, 이미 OutboundProduct가 있는 채널은 Product 생성을
     * 건너뜁니다.
     *
     * @param productGroup 활성화된 상품그룹
     */
    public void createOutboxAndProducts(ProductGroup productGroup) {
        List<SellerSalesChannel> connectedChannels =
                sellerSalesChannelReadManager.findConnectedBySellerId(productGroup.sellerId());

        if (connectedChannels.isEmpty()) {
            log.info(
                    "CONNECTED 판매채널 없음, 외부 연동 Outbox 생략: sellerId={}",
                    productGroup.sellerIdValue());
            return;
        }

        List<OutboundSyncOutbox> existingPendingOutboxes =
                outboxReadManager.findPendingByProductGroupId(productGroup.id());

        Set<Long> channelsWithPendingOutbox =
                existingPendingOutboxes.stream()
                        .map(OutboundSyncOutbox::salesChannelIdValue)
                        .collect(Collectors.toSet());

        List<SellerSalesChannel> channelsNeedingOutbox =
                connectedChannels.stream()
                        .filter(
                                ch ->
                                        !channelsWithPendingOutbox.contains(
                                                ch.salesChannelId().value()))
                        .toList();

        if (!channelsNeedingOutbox.isEmpty()) {
            List<OutboundSyncOutbox> outboxes =
                    outboundSyncCommandFactory.createOutboxes(
                            productGroup.id(), productGroup.sellerId(), channelsNeedingOutbox);
            outboxCommandManager.persistAll(outboxes);
        }

        List<SellerSalesChannel> channelsWithoutProduct =
                connectedChannels.stream()
                        .filter(
                                channel ->
                                        !outboundProductReadManager
                                                .existsByProductGroupIdAndSalesChannelId(
                                                        productGroup.idValue(),
                                                        channel.salesChannelId().value()))
                        .toList();

        if (!channelsWithoutProduct.isEmpty()) {
            List<OutboundProduct> outboundProducts =
                    outboundSyncCommandFactory.createOutboundProducts(
                            productGroup.id(), channelsWithoutProduct);
            outboundProductCommandManager.persistAll(outboundProducts);
        }

        log.info(
                "외부 연동 Outbox 생성 완료: productGroupId={}, newOutboxCount={}, skippedOutboxCount={},"
                        + " newProductCount={}",
                productGroup.idValue(),
                channelsNeedingOutbox.size(),
                channelsWithPendingOutbox.size(),
                channelsWithoutProduct.size());
    }
}
