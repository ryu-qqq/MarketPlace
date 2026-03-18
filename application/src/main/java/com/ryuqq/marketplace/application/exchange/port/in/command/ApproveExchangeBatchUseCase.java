package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ApproveExchangeBatchCommand;

/** 교환 승인 일괄 처리 UseCase (수거 시작). */
public interface ApproveExchangeBatchUseCase {
    BatchProcessingResult<String> execute(ApproveExchangeBatchCommand command);
}
