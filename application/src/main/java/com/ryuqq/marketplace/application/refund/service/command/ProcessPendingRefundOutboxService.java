package com.ryuqq.marketplace.application.refund.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.ProcessPendingRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.internal.RefundOutboxRelayProcessor;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxReadManager;
import com.ryuqq.marketplace.application.refund.port.in.command.ProcessPendingRefundOutboxUseCase;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** PENDING 환불 아웃박스를 조회하여 SQS로 발행하는 서비스. */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "refund-outbox")
public class ProcessPendingRefundOutboxService implements ProcessPendingRefundOutboxUseCase {

    private final RefundOutboxReadManager outboxReadManager;
    private final RefundOutboxRelayProcessor relayProcessor;

    public ProcessPendingRefundOutboxService(
            RefundOutboxReadManager outboxReadManager,
            RefundOutboxRelayProcessor relayProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.relayProcessor = relayProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingRefundOutboxCommand command) {
        List<RefundOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (RefundOutbox outbox : outboxes) {
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
