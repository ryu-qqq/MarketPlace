package com.ryuqq.marketplace.application.cancel.port.in.command;

import com.ryuqq.marketplace.application.cancel.dto.command.ApproveCancelBatchCommand;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;

/** 취소 승인 일괄 처리 UseCase. */
public interface ApproveCancelBatchUseCase {
    BatchProcessingResult<String> execute(ApproveCancelBatchCommand command);
}
