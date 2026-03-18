package com.ryuqq.marketplace.application.refund.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.HoldRefundBatchCommand;

/** 환불 보류/보류 해제 일괄 처리 UseCase. */
public interface HoldRefundBatchUseCase {

    BatchProcessingResult<String> execute(HoldRefundBatchCommand command);
}
