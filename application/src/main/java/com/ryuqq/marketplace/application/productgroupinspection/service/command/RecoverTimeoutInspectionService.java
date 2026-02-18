package com.ryuqq.marketplace.application.productgroupinspection.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productgroupinspection.dto.command.RecoverTimeoutInspectionCommand;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxReadManager;
import com.ryuqq.marketplace.application.productgroupinspection.port.in.command.RecoverTimeoutInspectionUseCase;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RecoverTimeoutInspectionService - 타임아웃 검수 Outbox 복구 서비스.
 *
 * <p>PROCESSING 상태에서 타임아웃된 좀비 Outbox를 PENDING으로 복구합니다. 재처리는 다음 주기의
 * ProcessPendingInspectionService에서 수행됩니다.
 */
@Service
public class RecoverTimeoutInspectionService implements RecoverTimeoutInspectionUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutInspectionService.class);

    private final ProductGroupInspectionOutboxReadManager outboxReadManager;
    private final ProductGroupInspectionOutboxCommandManager outboxCommandManager;

    public RecoverTimeoutInspectionService(
            ProductGroupInspectionOutboxReadManager outboxReadManager,
            ProductGroupInspectionOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(RecoverTimeoutInspectionCommand command) {
        List<ProductGroupInspectionOutbox> outboxes =
                outboxReadManager.findInProgressTimeoutOutboxes(
                        command.timeoutThreshold(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;
        Instant now = Instant.now();

        for (ProductGroupInspectionOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(now);
                outboxCommandManager.persist(outbox);
                successCount++;
            } catch (Exception e) {
                log.error(
                        "검수 Outbox 복구 실패: outboxId={}, productGroupId={}, error={}",
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
