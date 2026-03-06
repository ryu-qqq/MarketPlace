package com.ryuqq.marketplace.application.outboundsync.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.outboundsync.dto.command.ProcessPendingOutboundSyncCommand;
import com.ryuqq.marketplace.application.outboundsync.internal.OutboundSyncRelayProcessor;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.outboundsync.port.in.command.ProcessPendingOutboundSyncUseCase;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * PENDING 상태의 OutboundSync Outbox를 조회하여 SQS로 발행하는 서비스.
 *
 * <p>Intelligence Pipeline의 ProcessPendingIntelligenceService 패턴과 동일합니다.
 */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "outbound-sync")
public class ProcessPendingOutboundSyncService implements ProcessPendingOutboundSyncUseCase {

    private final OutboundSyncOutboxReadManager outboxReadManager;
    private final OutboundSyncRelayProcessor relayProcessor;

    public ProcessPendingOutboundSyncService(
            OutboundSyncOutboxReadManager outboxReadManager,
            OutboundSyncRelayProcessor relayProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.relayProcessor = relayProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingOutboundSyncCommand command) {
        List<OutboundSyncOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (OutboundSyncOutbox outbox : outboxes) {
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
