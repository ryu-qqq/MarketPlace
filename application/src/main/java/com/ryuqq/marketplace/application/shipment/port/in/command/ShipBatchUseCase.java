package com.ryuqq.marketplace.application.shipment.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;

/** 송장등록 일괄 처리 UseCase. */
public interface ShipBatchUseCase {

    BatchProcessingResult<String> execute(ShipBatchCommand command);
}
