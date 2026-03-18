package com.ryuqq.marketplace.application.refund.internal;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxReadManager;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 환불 아웃박스 처리기.
 *
 * <p>PENDING 상태의 아웃박스를 처리하여 외부 채널에 환불 상태를 동기화합니다.
 */
@Component
public class RefundOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(RefundOutboxProcessor.class);

    private final RefundOutboxCommandManager outboxCommandManager;
    private final RefundOutboxReadManager outboxReadManager;
    private final TimeProvider timeProvider;

    public RefundOutboxProcessor(
            RefundOutboxCommandManager outboxCommandManager,
            RefundOutboxReadManager outboxReadManager,
            TimeProvider timeProvider) {
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.timeProvider = timeProvider;
    }

    public boolean processOutbox(RefundOutbox outbox) {
        Instant now = timeProvider.now();
        Long outboxId = outbox.idValue();

        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            // TODO: 외부 채널 Strategy 라우팅 + API 호출
            // RefundOutboundStrategy strategy = strategyRouter.route(outbox);
            // RefundOutboundResult result = strategy.execute(outbox);

            // 임시: Strategy 미구현 -> 바로 COMPLETED 처리
            outbox.complete(timeProvider.now());
            outboxCommandManager.persist(outbox);

            return true;
        } catch (Exception e) {
            log.error(
                    "환불 Outbox 처리 실패: outboxId={}, orderItemId={}, type={}, error={}",
                    outboxId,
                    outbox.orderItemId(),
                    outbox.outboxType(),
                    e.getMessage(),
                    e);
            persistFailureWithReRead(outboxId, true, e.getMessage());
            return false;
        }
    }

    private void persistFailureWithReRead(Long outboxId, boolean retryable, String errorMessage) {
        try {
            RefundOutbox freshOutbox = outboxReadManager.getById(outboxId);
            freshOutbox.recordFailure(retryable, errorMessage, timeProvider.now());
            outboxCommandManager.persist(freshOutbox);
        } catch (Exception reReadEx) {
            log.warn(
                    "환불 Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    reReadEx.getMessage());
        }
    }
}
