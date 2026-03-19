package com.ryuqq.marketplace.application.shipment.port.in.command;

import com.ryuqq.marketplace.application.shipment.dto.command.ExecuteShipmentOutboxCommand;

/**
 * 배송 Outbox 실행 유스케이스 포트.
 *
 * <p>SQS Consumer에서 수신한 배송 Outbox를 처리합니다.
 */
public interface ExecuteShipmentOutboxUseCase {

    void execute(ExecuteShipmentOutboxCommand command);
}
