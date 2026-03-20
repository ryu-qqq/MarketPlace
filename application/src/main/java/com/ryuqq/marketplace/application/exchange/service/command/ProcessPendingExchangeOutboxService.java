package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ProcessPendingExchangeOutboxCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangeOutboxRelayProcessor;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxReadManager;
import com.ryuqq.marketplace.application.exchange.port.in.command.ProcessPendingExchangeOutboxUseCase;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** PENDING 교환 아웃박스를 조회하여 SQS로 발행하는 서비스. */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "exchange-outbox")
public class ProcessPendingExchangeOutboxService implements ProcessPendingExchangeOutboxUseCase {

    private final ExchangeOutboxReadManager outboxReadManager;
    private final ExchangeOutboxRelayProcessor relayProcessor;
    private final ExchangeCommandFactory commandFactory;

    public ProcessPendingExchangeOutboxService(
            ExchangeOutboxReadManager outboxReadManager,
            ExchangeOutboxRelayProcessor relayProcessor,
            ExchangeCommandFactory commandFactory) {
        this.outboxReadManager = outboxReadManager;
        this.relayProcessor = relayProcessor;
        this.commandFactory = commandFactory;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingExchangeOutboxCommand command) {
        Instant beforeTime = commandFactory.calculatePendingThreshold(command.delaySeconds());
        List<ExchangeOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(beforeTime, command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ExchangeOutbox outbox : outboxes) {
            boolean success = relayProcessor.relay(outbox);
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
