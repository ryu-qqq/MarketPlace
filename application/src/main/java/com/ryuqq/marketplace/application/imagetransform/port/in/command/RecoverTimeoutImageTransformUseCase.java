package com.ryuqq.marketplace.application.imagetransform.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RecoverTimeoutImageTransformCommand;

/** 이미지 변환 Outbox 타임아웃 복구 UseCase. */
public interface RecoverTimeoutImageTransformUseCase {

    SchedulerBatchProcessingResult execute(RecoverTimeoutImageTransformCommand command);
}
