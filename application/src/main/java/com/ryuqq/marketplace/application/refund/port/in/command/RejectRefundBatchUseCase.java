package com.ryuqq.marketplace.application.refund.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.RejectRefundBatchCommand;

/** 환불 거절 일괄 처리 UseCase. */
public interface RejectRefundBatchUseCase {
    BatchProcessingResult<String> execute(RejectRefundBatchCommand command);
}
