package com.ryuqq.marketplace.application.refund.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand;

/** 환불 요청 일괄 처리 UseCase. */
public interface RequestRefundBatchUseCase {
    BatchProcessingResult<String> execute(RequestRefundBatchCommand command);
}
