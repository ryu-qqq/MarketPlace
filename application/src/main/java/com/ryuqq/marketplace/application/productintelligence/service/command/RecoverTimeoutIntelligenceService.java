package com.ryuqq.marketplace.application.productintelligence.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productintelligence.dto.command.RecoverTimeoutIntelligenceCommand;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligenceOutboxCommandManager;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligenceOutboxReadManager;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.RecoverTimeoutIntelligenceUseCase;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RecoverTimeoutIntelligenceService - 타임아웃 Intelligence Outbox 복구 서비스.
 *
 * <p>SENT 상태에서 타임아웃된 좀비 Outbox를 PENDING으로 복구합니다. 재처리는 다음 주기의 ProcessPendingIntelligenceService에서
 * 수행됩니다.
 */
@Service
public class RecoverTimeoutIntelligenceService implements RecoverTimeoutIntelligenceUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutIntelligenceService.class);

    private final IntelligenceOutboxReadManager outboxReadManager;
    private final IntelligenceOutboxCommandManager outboxCommandManager;

    public RecoverTimeoutIntelligenceService(
            IntelligenceOutboxReadManager outboxReadManager,
            IntelligenceOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(RecoverTimeoutIntelligenceCommand command) {
        List<IntelligenceOutbox> outboxes =
                outboxReadManager.findInProgressTimeoutOutboxes(
                        command.timeoutThreshold(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;
        Instant now = Instant.now();

        for (IntelligenceOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(now);
                outboxCommandManager.persist(outbox);
                successCount++;
            } catch (Exception e) {
                log.error(
                        "Intelligence Outbox 복구 실패: outboxId={}, productGroupId={}, error={}",
                        outbox.idValue(),
                        outbox.productGroupId(),
                        e.getMessage(),
                        e);
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
