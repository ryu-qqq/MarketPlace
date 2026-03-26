package com.ryuqq.marketplace.application.refund.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.CollectRefundBatchCommand;

/** 환불 수거 완료 일괄 처리 UseCase. */
public interface CollectRefundBatchUseCase {
    BatchProcessingResult<String> execute(CollectRefundBatchCommand command);
}
