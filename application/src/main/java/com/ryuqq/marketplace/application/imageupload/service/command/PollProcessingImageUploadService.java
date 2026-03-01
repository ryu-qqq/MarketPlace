package com.ryuqq.marketplace.application.imageupload.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.dto.command.PollProcessingImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.internal.ImageUploadPollingProcessor;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxReadManager;
import com.ryuqq.marketplace.application.imageupload.port.in.command.PollProcessingImageUploadUseCase;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * PROCESSING 이미지 업로드 Outbox 폴링 서비스.
 *
 * <p>각 Outbox 폴링은 ImageUploadPollingProcessor에서 수행됩니다.
 */
@Service
public class PollProcessingImageUploadService implements PollProcessingImageUploadUseCase {

    private final ImageUploadOutboxReadManager outboxReadManager;
    private final ImageUploadPollingProcessor pollingProcessor;

    public PollProcessingImageUploadService(
            ImageUploadOutboxReadManager outboxReadManager,
            ImageUploadPollingProcessor pollingProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.pollingProcessor = pollingProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(PollProcessingImageUploadCommand command) {
        List<ImageUploadOutbox> outboxes =
                outboxReadManager.findProcessingOutboxes(command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ImageUploadOutbox outbox : outboxes) {
            boolean completed = pollingProcessor.pollOutbox(outbox);
            if (completed) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
