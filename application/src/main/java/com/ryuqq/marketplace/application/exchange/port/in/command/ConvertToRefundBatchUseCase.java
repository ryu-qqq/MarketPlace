package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ConvertToRefundBatchCommand;

/** 교환 건 환불 전환 일괄 처리 UseCase. */
public interface ConvertToRefundBatchUseCase {
    BatchProcessingResult<String> execute(ConvertToRefundBatchCommand command);
}
