package com.ryuqq.marketplace.application.imagetransform.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagetransform.dto.command.PollProcessingImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.internal.ImageTransformPollingProcessor;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxReadManager;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.PollProcessingImageTransformUseCase;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * PROCESSING 이미지 변환 Outbox 폴링 서비스.
 *
 * <p>각 Outbox 폴링은 ImageTransformPollingProcessor에서 수행됩니다.
 */
@Service
public class PollProcessingImageTransformService implements PollProcessingImageTransformUseCase {

    private final ImageTransformOutboxReadManager outboxReadManager;
    private final ImageTransformPollingProcessor pollingProcessor;

    public PollProcessingImageTransformService(
            ImageTransformOutboxReadManager outboxReadManager,
            ImageTransformPollingProcessor pollingProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.pollingProcessor = pollingProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(PollProcessingImageTransformCommand command) {
        List<ImageTransformOutbox> outboxes =
                outboxReadManager.findProcessingOutboxes(command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ImageTransformOutbox outbox : outboxes) {
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
