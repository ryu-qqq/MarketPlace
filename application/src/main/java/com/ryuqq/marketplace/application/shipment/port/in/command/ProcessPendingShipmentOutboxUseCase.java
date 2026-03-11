package com.ryuqq.marketplace.application.shipment.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ProcessPendingShipmentOutboxCommand;

/** 대기 중인 배송 아웃박스 처리 UseCase. */
public interface ProcessPendingShipmentOutboxUseCase {

    SchedulerBatchProcessingResult execute(ProcessPendingShipmentOutboxCommand command);
}
