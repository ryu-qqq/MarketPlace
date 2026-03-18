package com.ryuqq.marketplace.application.exchange.dto.command;

import java.time.Instant;

/** PENDING 상태 교환 아웃박스 처리 명령. */
public record ProcessPendingExchangeOutboxCommand(int batchSize, int delaySeconds) {

    public Instant beforeTime() {
        return Instant.now().minusSeconds(delaySeconds);
    }
}
