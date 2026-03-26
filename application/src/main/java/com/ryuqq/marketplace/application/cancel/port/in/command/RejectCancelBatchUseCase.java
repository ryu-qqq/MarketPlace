package com.ryuqq.marketplace.application.cancel.port.in.command;

import com.ryuqq.marketplace.application.cancel.dto.command.RejectCancelBatchCommand;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;

/** 취소 거절 일괄 처리 UseCase. */
public interface RejectCancelBatchUseCase {
    BatchProcessingResult<String> execute(RejectCancelBatchCommand command);
}
