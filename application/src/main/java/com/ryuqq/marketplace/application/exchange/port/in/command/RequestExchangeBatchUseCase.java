package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand;

/** 교환 요청 일괄 처리 UseCase. */
public interface RequestExchangeBatchUseCase {
    BatchProcessingResult<String> execute(RequestExchangeBatchCommand command);
}
