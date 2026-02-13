package com.ryuqq.marketplace.application.shipment.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;

/** 발주확인 일괄 처리 UseCase. */
public interface ConfirmShipmentBatchUseCase {

    BatchProcessingResult<String> execute(ConfirmShipmentBatchCommand command);
}
