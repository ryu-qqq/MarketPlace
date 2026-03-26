package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.RecoverTimeoutExchangeOutboxCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxReadManager;
import com.ryuqq.marketplace.application.exchange.port.in.command.RecoverTimeoutExchangeOutboxUseCase;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.time.Instant;
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
    private final ExchangeCommandFactory commandFactory;

    public RecoverTimeoutExchangeOutboxService(
            ExchangeOutboxReadManager outboxReadManager,
            ExchangeOutboxCommandManager outboxCommandManager,
            ExchangeCommandFactory commandFactory) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public SchedulerBatchProcessingResult execute(RecoverTimeoutExchangeOutboxCommand command) {
        Instant timeoutThreshold =
                commandFactory.calculateTimeoutThreshold(command.timeoutSeconds());
        List<ExchangeOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        timeoutThreshold, command.batchSize());

        int total = outboxes.size();
        int success = 0;
        int failed = 0;

        for (ExchangeOutbox outbox : outboxes) {
            try {
                StatusChangeContext<Long> ctx =
                        commandFactory.createOutboxChangeContext(outbox.idValue());
                outbox.recoverFromTimeout(ctx.changedAt());
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
