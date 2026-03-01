package com.ryuqq.marketplace.application.imageupload.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.dto.command.PollProcessingImageUploadCommand;

/** PROCESSING 이미지 업로드 Outbox 폴링 UseCase. */
public interface PollProcessingImageUploadUseCase {

    SchedulerBatchProcessingResult execute(PollProcessingImageUploadCommand command);
}
