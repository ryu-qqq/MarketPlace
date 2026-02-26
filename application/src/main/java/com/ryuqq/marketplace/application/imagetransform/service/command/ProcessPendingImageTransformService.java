package com.ryuqq.marketplace.application.imagetransform.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagetransform.dto.command.ProcessPendingImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.internal.ImageTransformOutboxProcessor;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxReadManager;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.ProcessPendingImageTransformUseCase;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * PENDING 이미지 변환 Outbox 배치 처리 서비스.
 *
 * <p>각 Outbox 처리는 ImageTransformOutboxProcessor에서 수행됩니다.
 */
@Service
public class ProcessPendingImageTransformService implements ProcessPendingImageTransformUseCase {

    private final ImageTransformOutboxReadManager outboxReadManager;
    private final ImageTransformOutboxProcessor outboxProcessor;

    public ProcessPendingImageTransformService(
            ImageTransformOutboxReadManager outboxReadManager,
            ImageTransformOutboxProcessor outboxProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.outboxProcessor = outboxProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingImageTransformCommand command) {
        List<ImageTransformOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ImageTransformOutbox outbox : outboxes) {
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
