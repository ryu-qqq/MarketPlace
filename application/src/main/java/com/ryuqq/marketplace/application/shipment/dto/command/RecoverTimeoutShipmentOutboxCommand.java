package com.ryuqq.marketplace.application.shipment.dto.command;

import java.time.Instant;

/**
 * 타임아웃 배송 아웃박스 복구 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 * @param timeoutSeconds 타임아웃 임계값 (초)
 */
public record RecoverTimeoutShipmentOutboxCommand(int batchSize, long timeoutSeconds) {

    public static RecoverTimeoutShipmentOutboxCommand of(int batchSize, long timeoutSeconds) {
        return new RecoverTimeoutShipmentOutboxCommand(batchSize, timeoutSeconds);
    }

    public Instant timeoutThreshold() {
        return Instant.now().minusSeconds(timeoutSeconds);
    }
}
