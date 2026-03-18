package com.ryuqq.marketplace.application.exchange.dto.command;

import java.time.Instant;

/** 타임아웃 교환 아웃박스 복구 명령. */
public record RecoverTimeoutExchangeOutboxCommand(int batchSize, long timeoutSeconds) {

    public Instant timeoutThreshold() {
        return Instant.now().minusSeconds(timeoutSeconds);
    }
}
