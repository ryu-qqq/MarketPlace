package com.ryuqq.marketplace.application.imagetransform.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagetransform.dto.command.ProcessPendingImageTransformCommand;

/** PENDING 이미지 변환 Outbox 처리 UseCase. */
public interface ProcessPendingImageTransformUseCase {

    SchedulerBatchProcessingResult execute(ProcessPendingImageTransformCommand command);
}
