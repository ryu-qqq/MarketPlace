package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.CollectExchangeBatchCommand;

/** 교환 수거 완료 일괄 처리 UseCase. */
public interface CollectExchangeBatchUseCase {
    BatchProcessingResult<String> execute(CollectExchangeBatchCommand command);
}
