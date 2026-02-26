package com.ryuqq.marketplace.application.productintelligence.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productintelligence.dto.command.ProcessPendingIntelligenceCommand;
import com.ryuqq.marketplace.application.productintelligence.internal.IntelligenceRelayProcessor;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligenceOutboxReadManager;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.ProcessPendingIntelligenceUseCase;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * ProcessPendingIntelligenceService - Intelligence Outbox Relay 서비스.
 *
 * <p>PENDING Outbox를 조회하여 IntelligenceRelayProcessor에 relay를 위임합니다.
 */
@Service
@ConditionalOnProperty(name = "intelligence.pipeline.enabled", havingValue = "true")
public class ProcessPendingIntelligenceService implements ProcessPendingIntelligenceUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(ProcessPendingIntelligenceService.class);

    private final IntelligenceOutboxReadManager outboxReadManager;
    private final IntelligenceRelayProcessor relayProcessor;

    public ProcessPendingIntelligenceService(
            IntelligenceOutboxReadManager outboxReadManager,
            IntelligenceRelayProcessor relayProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.relayProcessor = relayProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingIntelligenceCommand command) {
        List<IntelligenceOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (IntelligenceOutbox outbox : outboxes) {
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
