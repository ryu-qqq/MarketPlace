package com.ryuqq.marketplace.application.imageupload.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.dto.command.RecoverFailedImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxReadManager;
import com.ryuqq.marketplace.application.imageupload.port.in.command.RecoverFailedImageUploadUseCase;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * FAILED 이미지 업로드 Outbox 복구 서비스.
 *
 * <p>복구 가능한 FAILED Outbox를 찾아 PENDING으로 초기화합니다.
 */
@Service
public class RecoverFailedImageUploadService implements RecoverFailedImageUploadUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverFailedImageUploadService.class);

    private final ImageUploadOutboxReadManager outboxReadManager;
    private final ImageUploadOutboxCommandManager outboxCommandManager;

    public RecoverFailedImageUploadService(
            ImageUploadOutboxReadManager outboxReadManager,
            ImageUploadOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    public SchedulerBatchProcessingResult execute(RecoverFailedImageUploadCommand command) {
        Instant failedBefore = Instant.now().minusSeconds(command.failedAfterSeconds());

        List<ImageUploadOutbox> outboxes =
                outboxReadManager.findRecoverableFailedOutboxes(failedBefore, command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ImageUploadOutbox outbox : outboxes) {
            try {
                outbox.resetForRetry(Instant.now());
                outboxCommandManager.persist(outbox);
                successCount++;

                log.info(
                        "FAILED Outbox 복구 완료: outboxId={}, sourceType={}, sourceId={}",
                        outbox.idValue(),
                        outbox.sourceType(),
                        outbox.sourceId());
            } catch (Exception e) {
                failedCount++;
                log.error(
                        "FAILED Outbox 복구 실패: outboxId={}, error={}",
                        outbox.idValue(),
                        e.getMessage(),
                        e);
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
