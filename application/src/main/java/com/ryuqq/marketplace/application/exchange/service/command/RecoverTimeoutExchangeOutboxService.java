package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.exchange.dto.command.RecoverTimeoutExchangeOutboxCommand;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxReadManager;
import com.ryuqq.marketplace.application.exchange.port.in.command.RecoverTimeoutExchangeOutboxUseCase;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 타임아웃 교환 아웃박스 복구 서비스. */
@Service
public class RecoverTimeoutExchangeOutboxService implements RecoverTimeoutExchangeOutboxUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutExchangeOutboxService.class);

    private final ExchangeOutboxReadManager outboxReadManager;
    private final ExchangeOutboxCommandManager outboxCommandManager;
    private final TimeProvider timeProvider;

    public RecoverTimeoutExchangeOutboxService(
            ExchangeOutboxReadManager outboxReadManager,
            ExchangeOutboxCommandManager outboxCommandManager,
            TimeProvider timeProvider) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public SchedulerBatchProcessingResult execute(RecoverTimeoutExchangeOutboxCommand command) {
        List<ExchangeOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        command.timeoutThreshold(), command.batchSize());

        int total = outboxes.size();
        int success = 0;
        int failed = 0;

        for (ExchangeOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(timeProvider.now());
                outboxCommandManager.persist(outbox);
                success++;
            } catch (Exception e) {
                log.warn(
                        "교환 Outbox 타임아웃 복구 실패: outboxId={}, error={}",
                        outbox.idValue(),
                        e.getMessage());
                failed++;
            }
        }

        return new SchedulerBatchProcessingResult(total, success, failed);
    }
}
