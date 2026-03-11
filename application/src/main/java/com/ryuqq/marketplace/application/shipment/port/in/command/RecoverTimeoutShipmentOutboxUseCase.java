package com.ryuqq.marketplace.application.shipment.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.RecoverTimeoutShipmentOutboxCommand;

/** 타임아웃된 배송 아웃박스 복구 UseCase. */
public interface RecoverTimeoutShipmentOutboxUseCase {

    SchedulerBatchProcessingResult execute(RecoverTimeoutShipmentOutboxCommand command);
}
