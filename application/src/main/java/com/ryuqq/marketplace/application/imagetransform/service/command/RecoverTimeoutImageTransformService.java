package com.ryuqq.marketplace.application.imagetransform.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RecoverTimeoutImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxReadManager;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.RecoverTimeoutImageTransformUseCase;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 타임아웃 이미지 변환 Outbox 복구 서비스.
 *
 * <p>PROCESSING 상태에서 타임아웃된 좀비 Outbox를 PENDING으로 복구합니다. 재처리는 다음 주기의
 * ProcessPendingImageTransformService에서 수행됩니다.
 */
@Service
public class RecoverTimeoutImageTransformService implements RecoverTimeoutImageTransformUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutImageTransformService.class);

    private final ImageTransformOutboxReadManager outboxReadManager;
    private final ImageTransformOutboxCommandManager outboxCommandManager;

    public RecoverTimeoutImageTransformService(
            ImageTransformOutboxReadManager outboxReadManager,
            ImageTransformOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(RecoverTimeoutImageTransformCommand command) {
        List<ImageTransformOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        command.timeoutThreshold(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;
        Instant now = Instant.now();

        for (ImageTransformOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(now);
                outboxCommandManager.persist(outbox);
                successCount++;
            } catch (Exception e) {
                log.error(
                        "이미지 변환 Outbox 복구 실패: outboxId={}, sourceType={}, sourceImageId={},"
                                + " error={}",
                        outbox.idValue(),
                        outbox.sourceType(),
                        outbox.sourceImageId(),
                        e.getMessage(),
                        e);
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
