package com.ryuqq.marketplace.application.outboundsync.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.outboundsync.dto.command.RecoverTimeoutOutboundSyncCommand;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.outboundsync.port.in.command.RecoverTimeoutOutboundSyncUseCase;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PROCESSING 타임아웃된 OutboundSync Outbox를 PENDING으로 복구하는 서비스.
 *
 * <p>RecoverTimeoutIntelligenceService 패턴과 동일합니다.
 */
@Service
public class RecoverTimeoutOutboundSyncService implements RecoverTimeoutOutboundSyncUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutOutboundSyncService.class);

    private final OutboundSyncOutboxReadManager outboxReadManager;
    private final OutboundSyncOutboxCommandManager outboxCommandManager;

    public RecoverTimeoutOutboundSyncService(
            OutboundSyncOutboxReadManager outboxReadManager,
            OutboundSyncOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(RecoverTimeoutOutboundSyncCommand command) {
        List<OutboundSyncOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        command.timeoutThreshold(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;
        Instant now = Instant.now();

        for (OutboundSyncOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(now);
                outboxCommandManager.persist(outbox);
                successCount++;
            } catch (Exception e) {
                log.error(
                        "OutboundSync Outbox 복구 실패: outboxId={}, productGroupId={}, error={}",
                        outbox.idValue(),
                        outbox.productGroupIdValue(),
                        e.getMessage(),
                        e);
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
