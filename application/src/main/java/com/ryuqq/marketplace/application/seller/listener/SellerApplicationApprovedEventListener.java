package com.ryuqq.marketplace.application.seller.listener;

import com.ryuqq.marketplace.application.seller.internal.SellerAuthOutboxProcessor;
import com.ryuqq.marketplace.application.seller.manager.SellerAuthOutboxReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellerapplication.event.SellerApplicationApprovedEvent;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 셀러 입점 신청 승인 이벤트 리스너.
 *
 * <p>입점 신청이 승인되면 인증 서버로 Tenant/Organization 생성 요청을 처리합니다.
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>이벤트 수신 (원본 트랜잭션 커밋 후 발행됨)
 *   <li>비동기 스레드에서 PENDING 상태 Outbox 조회
 *   <li>SellerAuthOutboxProcessor로 처리 위임
 *   <li>실패 시 스케줄러에서 재시도
 * </ol>
 *
 * <p><strong>조건부 활성화</strong>: authhub.base-url 설정이 있을 때만 활성화됩니다.
 */
@Component
@ConditionalOnProperty(prefix = "authhub", name = "base-url")
public class SellerApplicationApprovedEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(SellerApplicationApprovedEventListener.class);

    private final SellerAuthOutboxReadManager outboxReadManager;
    private final SellerAuthOutboxProcessor outboxProcessor;

    public SellerApplicationApprovedEventListener(
            SellerAuthOutboxReadManager outboxReadManager,
            SellerAuthOutboxProcessor outboxProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.outboxProcessor = outboxProcessor;
    }

    /**
     * 셀러 입점 승인 이벤트를 비동기로 처리합니다.
     *
     * @param event 승인 이벤트
     */
    @Async
    @EventListener
    public void handleSellerApplicationApproved(SellerApplicationApprovedEvent event) {
        SellerId sellerId = event.approvedSellerId();

        log.info(
                "셀러 입점 승인 이벤트 수신: applicationId={}, sellerId={}, processedBy={}",
                event.applicationId().value(),
                sellerId.value(),
                event.processedBy());

        Optional<SellerAuthOutbox> outboxOpt = outboxReadManager.findPendingBySellerId(sellerId);

        if (outboxOpt.isEmpty()) {
            log.warn("처리할 Outbox가 없습니다: sellerId={}", sellerId.value());
            return;
        }

        outboxProcessor.processOutbox(outboxOpt.get());
    }
}
