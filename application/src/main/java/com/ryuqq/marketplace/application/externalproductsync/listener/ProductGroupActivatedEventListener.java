package com.ryuqq.marketplace.application.externalproductsync.listener;

import com.ryuqq.marketplace.application.externalproductsync.manager.ExternalProductSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.domain.externalproductsync.aggregate.ExternalProductSyncOutbox;
import com.ryuqq.marketplace.domain.externalproductsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.event.ProductGroupActivatedEvent;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 상품그룹 활성화 이벤트 리스너.
 *
 * <p>검수 통과 후 상품그룹이 활성화되면 셀러의 CONNECTED 채널별로 외부 연동 Outbox를 생성합니다.
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>이벤트 수신 (원본 트랜잭션 커밋 후 발행됨)
 *   <li>비동기 스레드에서 셀러의 CONNECTED 판매채널 조회
 *   <li>채널별 ExternalProductSyncOutbox(CREATE) 생성
 *   <li>배치 저장
 * </ol>
 */
@Component
public class ProductGroupActivatedEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(ProductGroupActivatedEventListener.class);

    private final SellerSalesChannelReadManager sellerSalesChannelReadManager;
    private final ExternalProductSyncOutboxCommandManager outboxCommandManager;

    public ProductGroupActivatedEventListener(
            SellerSalesChannelReadManager sellerSalesChannelReadManager,
            ExternalProductSyncOutboxCommandManager outboxCommandManager) {
        this.sellerSalesChannelReadManager = sellerSalesChannelReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    /**
     * 상품그룹 활성화 이벤트를 비동기로 처리합니다.
     *
     * @param event 활성화 이벤트
     */
    @Async
    @EventListener
    public void handleProductGroupActivated(ProductGroupActivatedEvent event) {
        Long productGroupId = event.productGroupId().value();
        Long sellerId = event.sellerId().value();

        log.info("상품그룹 활성화 이벤트 수신: productGroupId={}, sellerId={}", productGroupId, sellerId);

        List<SellerSalesChannel> connectedChannels =
                sellerSalesChannelReadManager.findConnectedBySellerId(event.sellerId());

        if (connectedChannels.isEmpty()) {
            log.info("CONNECTED 판매채널이 없습니다: sellerId={}. 외부 연동 Outbox 생성 생략.", sellerId);
            return;
        }

        Instant now = Instant.now();
        List<ExternalProductSyncOutbox> outboxes =
                connectedChannels.stream()
                        .map(
                                channel ->
                                        ExternalProductSyncOutbox.forNew(
                                                productGroupId,
                                                channel.salesChannelIdValue(),
                                                sellerId,
                                                SyncType.CREATE,
                                                "{}",
                                                now))
                        .toList();

        outboxCommandManager.persistAll(outboxes);

        log.info(
                "외부 상품 연동 Outbox 생성 완료: productGroupId={}, sellerId={}, channelCount={}",
                productGroupId,
                sellerId,
                outboxes.size());
    }
}
