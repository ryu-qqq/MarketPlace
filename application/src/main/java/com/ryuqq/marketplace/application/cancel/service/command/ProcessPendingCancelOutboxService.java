package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.command.ProcessPendingCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.internal.CancelOutboxRelayProcessor;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxReadManager;
import com.ryuqq.marketplace.application.cancel.port.in.command.ProcessPendingCancelOutboxUseCase;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** PENDING 취소 아웃박스를 조회하여 SQS로 발행하는 서비스. */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "cancel-outbox")
public class ProcessPendingCancelOutboxService implements ProcessPendingCancelOutboxUseCase {

    private final CancelOutboxReadManager outboxReadManager;
    private final CancelOutboxRelayProcessor relayProcessor;
    private final CancelCommandFactory commandFactory;

    public ProcessPendingCancelOutboxService(
            CancelOutboxReadManager outboxReadManager,
            CancelOutboxRelayProcessor relayProcessor,
            CancelCommandFactory commandFactory) {
        this.outboxReadManager = outboxReadManager;
        this.relayProcessor = relayProcessor;
        this.commandFactory = commandFactory;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingCancelOutboxCommand command) {
        Instant beforeTime = commandFactory.calculateBeforeTime(command.delaySeconds());
        List<CancelOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(beforeTime, command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (CancelOutbox outbox : outboxes) {
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
