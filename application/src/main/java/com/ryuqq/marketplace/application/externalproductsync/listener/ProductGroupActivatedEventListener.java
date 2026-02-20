package com.ryuqq.marketplace.application.externalproductsync.listener;

import com.ryuqq.marketplace.application.externalproductsync.manager.ExternalProductSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.domain.externalproductsync.aggregate.ExternalProductSyncOutbox;
import com.ryuqq.marketplace.domain.externalproductsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.event.ProductGroupActivatedEvent;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 상품그룹 활성화 이벤트 리스너.
 *
 * <p>검수 통과 후 상품그룹이 활성화되면 셀러의 CONNECTED 채널별로 외부 연동 Outbox를 생성합니다.
 *
 * <p><strong>트랜잭션 원자성 보장</strong>: {@code @TransactionalEventListener(BEFORE_COMMIT)}를 사용하여 상품그룹 상태
 * 변경과 Outbox 저장이 동일 트랜잭션 내에서 수행됩니다. Outbox 생성 실패 시 전체 트랜잭션이 롤백됩니다.
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>이벤트 수신 (원본 트랜잭션 커밋 전 실행)
 *   <li>셀러의 CONNECTED 판매채널 조회
 *   <li>채널별 ExternalProductSyncOutbox(CREATE) 생성
 *   <li>배치 저장 (원본 트랜잭션과 동일 커밋)
 * </ol>
 */
@Component
public class ProductGroupActivatedEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(ProductGroupActivatedEventListener.class);

    private final SellerSalesChannelReadManager sellerSalesChannelReadManager;
    private final ExternalProductSyncOutboxCommandManager outboxCommandManager;
    private final Clock clock;

    public ProductGroupActivatedEventListener(
            SellerSalesChannelReadManager sellerSalesChannelReadManager,
            ExternalProductSyncOutboxCommandManager outboxCommandManager,
            Clock clock) {
        this.sellerSalesChannelReadManager = sellerSalesChannelReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.clock = clock;
    }

    /**
     * 상품그룹 활성화 이벤트를 트랜잭션 커밋 전에 처리합니다.
     *
     * <p>원본 트랜잭션과 동일한 트랜잭션 내에서 실행되어 원자성을 보장합니다. 실패 시 전체 트랜잭션이 롤백됩니다.
     *
     * @param event 활성화 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleProductGroupActivated(ProductGroupActivatedEvent event) {
        Long productGroupId = event.productGroupId().value();
        Long sellerId = event.sellerId().value();

        try {
            log.info("상품그룹 활성화 이벤트 수신: productGroupId={}, sellerId={}", productGroupId, sellerId);

            List<SellerSalesChannel> connectedChannels =
                    sellerSalesChannelReadManager.findConnectedBySellerId(event.sellerId());

            if (connectedChannels.isEmpty()) {
                log.info("CONNECTED 판매채널이 없습니다: sellerId={}. 외부 연동 Outbox 생성 생략.", sellerId);
                return;
            }

            Instant now = clock.instant();
            List<ExternalProductSyncOutbox> outboxes =
                    connectedChannels.stream()
                            .map(
                                    channel ->
                                            ExternalProductSyncOutbox.forNew(
                                                    event.productGroupId(),
                                                    channel.salesChannelId(),
                                                    event.sellerId(),
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

        } catch (Exception e) {
            log.error(
                    "외부 상품 연동 Outbox 생성 실패: productGroupId={}, sellerId={}",
                    productGroupId,
                    sellerId,
                    e);
            throw e;
        }
    }
}
