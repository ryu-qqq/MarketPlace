package com.ryuqq.marketplace.application.imageupload.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.dto.command.RecoverTimeoutImageUploadCommand;

/**
 * 타임아웃된 이미지 업로드 Outbox 복구 UseCase.
 *
 * <p>PROCESSING 상태에서 일정 시간 이상 경과한 좀비 상태 Outbox를 PENDING으로 복구합니다.
 */
public interface RecoverTimeoutImageUploadUseCase {

    SchedulerBatchProcessingResult execute(RecoverTimeoutImageUploadCommand command);
}
