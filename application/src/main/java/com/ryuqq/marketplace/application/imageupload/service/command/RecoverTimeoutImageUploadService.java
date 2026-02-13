package com.ryuqq.marketplace.application.imageupload.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.dto.command.RecoverTimeoutImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxReadManager;
import com.ryuqq.marketplace.application.imageupload.port.in.command.RecoverTimeoutImageUploadUseCase;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RecoverTimeoutImageUploadService - 타임아웃 이미지 업로드 Outbox 복구 서비스.
 *
 * <p>PROCESSING 상태에서 타임아웃된 좀비 Outbox를 PENDING으로 복구합니다. 재처리는 다음 주기의
 * ProcessPendingImageUploadService에서 수행됩니다.
 */
@Service
public class RecoverTimeoutImageUploadService implements RecoverTimeoutImageUploadUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutImageUploadService.class);

    private final ImageUploadOutboxReadManager outboxReadManager;
    private final ImageUploadOutboxCommandManager outboxCommandManager;

    public RecoverTimeoutImageUploadService(
            ImageUploadOutboxReadManager outboxReadManager,
            ImageUploadOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(RecoverTimeoutImageUploadCommand command) {
        List<ImageUploadOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        command.timeoutThreshold(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;
        Instant now = Instant.now();

        for (ImageUploadOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(now);
                outboxCommandManager.persist(outbox);
                successCount++;
            } catch (Exception e) {
                log.error(
                        "이미지 업로드 Outbox 복구 실패: outboxId={}, sourceType={}, sourceId={}, error={}",
                        outbox.idValue(),
                        outbox.sourceType(),
                        outbox.sourceId(),
                        e.getMessage(),
                        e);
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
