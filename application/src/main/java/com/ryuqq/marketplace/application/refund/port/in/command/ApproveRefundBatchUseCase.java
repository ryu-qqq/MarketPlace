package com.ryuqq.marketplace.application.refund.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.ApproveRefundBatchCommand;

/** 환불 승인 일괄 처리 UseCase. */
public interface ApproveRefundBatchUseCase {
    BatchProcessingResult<String> execute(ApproveRefundBatchCommand command);
}
