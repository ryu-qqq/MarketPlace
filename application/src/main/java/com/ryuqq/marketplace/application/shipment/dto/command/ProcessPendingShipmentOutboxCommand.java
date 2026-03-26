package com.ryuqq.marketplace.application.shipment.dto.command;

/**
 * 대기 중인 배송 아웃박스 처리 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 * @param delaySeconds 생성 후 최소 대기 시간 (초)
 */
public record ProcessPendingShipmentOutboxCommand(int batchSize, int delaySeconds) {

    public static ProcessPendingShipmentOutboxCommand of(int batchSize, int delaySeconds) {
        return new ProcessPendingShipmentOutboxCommand(batchSize, delaySeconds);
    }
}
