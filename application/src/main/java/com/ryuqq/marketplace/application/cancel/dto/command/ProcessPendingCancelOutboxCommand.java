package com.ryuqq.marketplace.application.cancel.dto.command;

import java.time.Instant;

/** PENDING 상태 취소 아웃박스 처리 명령. */
public record ProcessPendingCancelOutboxCommand(int batchSize, int delaySeconds) {

    public Instant beforeTime() {
        return Instant.now().minusSeconds(delaySeconds);
    }
}
