package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.PrepareExchangeBatchCommand;

/** 교환 준비 완료 일괄 처리 UseCase. */
public interface PrepareExchangeBatchUseCase {
    BatchProcessingResult<String> execute(PrepareExchangeBatchCommand command);
}
