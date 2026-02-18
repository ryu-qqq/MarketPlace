package com.ryuqq.marketplace.application.imagetransform.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagetransform.dto.command.PollProcessingImageTransformCommand;

/** PROCESSING 이미지 변환 Outbox 폴링 UseCase. */
public interface PollProcessingImageTransformUseCase {

    SchedulerBatchProcessingResult execute(PollProcessingImageTransformCommand command);
}
