package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.HoldExchangeBatchCommand;

/** 교환 보류/보류 해제 일괄 처리 UseCase. */
public interface HoldExchangeBatchUseCase {

    BatchProcessingResult<String> execute(HoldExchangeBatchCommand command);
}
