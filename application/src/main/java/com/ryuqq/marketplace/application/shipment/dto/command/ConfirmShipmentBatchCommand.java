package com.ryuqq.marketplace.application.shipment.dto.command;

import java.util.List;

/**
 * 발주확인 일괄 처리 커맨드.
 *
 * @param shipmentIds 발주확인 대상 배송 ID 목록
 */
public record ConfirmShipmentBatchCommand(List<String> shipmentIds) {

    public ConfirmShipmentBatchCommand {
        shipmentIds = shipmentIds != null ? List.copyOf(shipmentIds) : List.of();
    }
}
