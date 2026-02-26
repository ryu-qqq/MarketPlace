package com.ryuqq.marketplace.application.seller.dto.command;

import java.time.Instant;

/**
 * 대기 중인 Outbox 처리 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 * @param delaySeconds 생성 후 최소 대기 시간 (초) - 즉시 처리 대상 제외용
 */
public record ProcessPendingOutboxCommand(int batchSize, int delaySeconds) {

    public static ProcessPendingOutboxCommand of(int batchSize, int delaySeconds) {
        return new ProcessPendingOutboxCommand(batchSize, delaySeconds);
    }

    public Instant beforeTime() {
        return Instant.now().minusSeconds(delaySeconds);
    }
}
