package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.RejectExchangeBatchCommand;

/** 교환 거절 일괄 처리 UseCase. */
public interface RejectExchangeBatchUseCase {
    BatchProcessingResult<String> execute(RejectExchangeBatchCommand command);
}
