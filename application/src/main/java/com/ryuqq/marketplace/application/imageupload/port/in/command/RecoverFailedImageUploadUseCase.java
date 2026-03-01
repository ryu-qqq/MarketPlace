package com.ryuqq.marketplace.application.imageupload.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.dto.command.RecoverFailedImageUploadCommand;

/** FAILED 이미지 업로드 Outbox 복구 UseCase. */
public interface RecoverFailedImageUploadUseCase {

    SchedulerBatchProcessingResult execute(RecoverFailedImageUploadCommand command);
}
