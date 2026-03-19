package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.command.ProcessPendingCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.internal.CancelOutboxRelayProcessor;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxReadManager;
import com.ryuqq.marketplace.application.cancel.port.in.command.ProcessPendingCancelOutboxUseCase;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** PENDING 취소 아웃박스를 조회하여 SQS로 발행하는 서비스. */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "cancel-outbox")
public class ProcessPendingCancelOutboxService implements ProcessPendingCancelOutboxUseCase {

    private final CancelOutboxReadManager outboxReadManager;
    private final CancelOutboxRelayProcessor relayProcessor;

    public ProcessPendingCancelOutboxService(
            CancelOutboxReadManager outboxReadManager, CancelOutboxRelayProcessor relayProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.relayProcessor = relayProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingCancelOutboxCommand command) {
        List<CancelOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(command.beforeTime(), command.batchSize());

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
