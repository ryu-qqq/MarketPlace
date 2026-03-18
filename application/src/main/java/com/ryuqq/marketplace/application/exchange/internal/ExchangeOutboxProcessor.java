package com.ryuqq.marketplace.application.exchange.internal;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxReadManager;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 교환 아웃박스 처리기.
 *
 * <p>PENDING 상태의 아웃박스를 처리하여 외부 채널에 교환 상태를 동기화합니다.
 */
@Component
public class ExchangeOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(ExchangeOutboxProcessor.class);

    private final ExchangeOutboxCommandManager outboxCommandManager;
    private final ExchangeOutboxReadManager outboxReadManager;
    private final TimeProvider timeProvider;

    public ExchangeOutboxProcessor(
            ExchangeOutboxCommandManager outboxCommandManager,
            ExchangeOutboxReadManager outboxReadManager,
            TimeProvider timeProvider) {
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.timeProvider = timeProvider;
    }

    public boolean processOutbox(ExchangeOutbox outbox) {
        Instant now = timeProvider.now();
        Long outboxId = outbox.idValue();

        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            // TODO: 외부 채널 Strategy 라우팅 + API 호출
            // ExchangeOutboundStrategy strategy = strategyRouter.route(outbox);
            // ExchangeOutboundResult result = strategy.execute(outbox);

            // 임시: Strategy 미구현 -> 바로 COMPLETED 처리
            outbox.complete(timeProvider.now());
            outboxCommandManager.persist(outbox);

            return true;
        } catch (Exception e) {
            log.error(
                    "교환 Outbox 처리 실패: outboxId={}, orderItemId={}, type={}, error={}",
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
            ExchangeOutbox freshOutbox = outboxReadManager.getById(outboxId);
            freshOutbox.recordFailure(retryable, errorMessage, timeProvider.now());
            outboxCommandManager.persist(freshOutbox);
        } catch (Exception reReadEx) {
            log.warn(
                    "교환 Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    reReadEx.getMessage());
        }
    }
}
