package com.ryuqq.marketplace.application.productintelligence.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductCommandManager;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundsync.factory.OutboundSyncCommandFactory;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productintelligence.vo.InspectionDecision;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 분석 판정 결과에 따른 ProductGroup 상태 전이 + 외부 연동 Outbox 생성 코디네이터.
 *
 * <p>{@code AggregateAnalysisService}에서 직접 호출되며, 동일 트랜잭션 내에서 실행됩니다.
 *
 * <p><strong>판정별 처리:</strong>
 *
 * <ul>
 *   <li>AUTO_APPROVED: 활성화(ACTIVE) + Outbox/OutboundProduct 생성
 *   <li>HUMAN_REVIEW: 검수대기(PENDING_REVIEW) 전환
 *   <li>AUTO_REJECTED: 반려(REJECTED) 전환
 * </ul>
 */
@Component
public class PostAnalysisProductGroupCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(PostAnalysisProductGroupCoordinator.class);

    private final ProductGroupReadManager productGroupReadManager;
    private final ProductGroupCommandManager productGroupCommandManager;
    private final SellerSalesChannelReadManager sellerSalesChannelReadManager;
    private final OutboundSyncCommandFactory outboundSyncCommandFactory;
    private final OutboundSyncOutboxCommandManager outboxCommandManager;
    private final OutboundProductCommandManager outboundProductCommandManager;
    private final OutboundProductReadManager outboundProductReadManager;

    public PostAnalysisProductGroupCoordinator(
            ProductGroupReadManager productGroupReadManager,
            ProductGroupCommandManager productGroupCommandManager,
            SellerSalesChannelReadManager sellerSalesChannelReadManager,
            OutboundSyncCommandFactory outboundSyncCommandFactory,
            OutboundSyncOutboxCommandManager outboxCommandManager,
            OutboundProductCommandManager outboundProductCommandManager,
            OutboundProductReadManager outboundProductReadManager) {
        this.productGroupReadManager = productGroupReadManager;
        this.productGroupCommandManager = productGroupCommandManager;
        this.sellerSalesChannelReadManager = sellerSalesChannelReadManager;
        this.outboundSyncCommandFactory = outboundSyncCommandFactory;
        this.outboxCommandManager = outboxCommandManager;
        this.outboundProductCommandManager = outboundProductCommandManager;
        this.outboundProductReadManager = outboundProductReadManager;
    }

    /** 분석 판정 결과에 따른 상품그룹 상태 전이 + 외부 연동 Outbox 생성. */
    public void execute(Long productGroupId, InspectionDecision decision, Instant now) {
        ProductGroup productGroup =
                productGroupReadManager.getById(ProductGroupId.of(productGroupId));

        if (decision.isApproved()) {
            activateAndCreateOutbox(productGroup, now);
        } else if (decision.needsReview()) {
            productGroup.pendingReview(now);
            productGroupCommandManager.persist(productGroup);
            log.info("상품그룹 검수대기 전환: productGroupId={}", productGroupId);
        } else if (decision.isRejected()) {
            productGroup.reject(now);
            productGroupCommandManager.persist(productGroup);
            log.info("상품그룹 자동반려: productGroupId={}", productGroupId);
        }
    }

    private void activateAndCreateOutbox(ProductGroup productGroup, Instant now) {
        productGroup.activate(now);
        productGroupCommandManager.persist(productGroup);

        List<SellerSalesChannel> connectedChannels =
                sellerSalesChannelReadManager.findConnectedBySellerId(productGroup.sellerId());

        if (connectedChannels.isEmpty()) {
            log.info(
                    "CONNECTED 판매채널 없음, 외부 연동 Outbox 생략: sellerId={}",
                    productGroup.sellerIdValue());
            return;
        }

        List<OutboundSyncOutbox> outboxes =
                outboundSyncCommandFactory.createOutboxes(
                        productGroup.id(), productGroup.sellerId(), connectedChannels);
        outboxCommandManager.persistAll(outboxes);

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
                "상품그룹 활성화 + 외부 연동 Outbox 생성 완료: productGroupId={}, outboxCount={},"
                        + " newOutboundProductCount={}",
                productGroup.idValue(),
                outboxes.size(),
                channelsWithoutProduct.size());
    }
}
