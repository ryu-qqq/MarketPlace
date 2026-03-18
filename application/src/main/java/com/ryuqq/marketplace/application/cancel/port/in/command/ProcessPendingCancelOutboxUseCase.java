package com.ryuqq.marketplace.application.cancel.port.in.command;

import com.ryuqq.marketplace.application.cancel.dto.command.ProcessPendingCancelOutboxCommand;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;

/** PENDING 취소 아웃박스 처리 UseCase. */
public interface ProcessPendingCancelOutboxUseCase {
    SchedulerBatchProcessingResult execute(ProcessPendingCancelOutboxCommand command);
}
