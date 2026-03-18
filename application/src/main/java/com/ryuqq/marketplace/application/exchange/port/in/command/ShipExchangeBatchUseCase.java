package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ShipExchangeBatchCommand;

/** 교환 재배송 일괄 처리 UseCase. */
public interface ShipExchangeBatchUseCase {
    BatchProcessingResult<String> execute(ShipExchangeBatchCommand command);
}
