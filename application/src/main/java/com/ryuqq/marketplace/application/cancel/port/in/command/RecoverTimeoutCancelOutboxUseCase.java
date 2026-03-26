package com.ryuqq.marketplace.application.cancel.port.in.command;

import com.ryuqq.marketplace.application.cancel.dto.command.RecoverTimeoutCancelOutboxCommand;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;

/** 타임아웃 취소 아웃박스 복구 UseCase. */
public interface RecoverTimeoutCancelOutboxUseCase {
    SchedulerBatchProcessingResult execute(RecoverTimeoutCancelOutboxCommand command);
}
