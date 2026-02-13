package com.ryuqq.marketplace.application.imageupload.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.dto.command.ProcessPendingImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.internal.ImageUploadOutboxProcessor;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxReadManager;
import com.ryuqq.marketplace.application.imageupload.port.in.command.ProcessPendingImageUploadUseCase;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * ProcessPendingImageUploadService - 대기 중인 이미지 업로드 Outbox 처리 서비스.
 *
 * <p>각 Outbox 처리는 ImageUploadOutboxProcessor에서 수행됩니다.
 */
@Service
public class ProcessPendingImageUploadService implements ProcessPendingImageUploadUseCase {

    private final ImageUploadOutboxReadManager outboxReadManager;
    private final ImageUploadOutboxProcessor outboxProcessor;

    public ProcessPendingImageUploadService(
            ImageUploadOutboxReadManager outboxReadManager,
            ImageUploadOutboxProcessor outboxProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.outboxProcessor = outboxProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingImageUploadCommand command) {
        List<ImageUploadOutbox> outboxes =
                outboxReadManager.findPendingOutboxesForRetry(
                        command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ImageUploadOutbox outbox : outboxes) {
            boolean success = outboxProcessor.processOutbox(outbox);
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
