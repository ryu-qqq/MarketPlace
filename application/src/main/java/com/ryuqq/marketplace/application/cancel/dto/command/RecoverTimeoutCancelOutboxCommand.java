package com.ryuqq.marketplace.application.cancel.dto.command;

import java.time.Instant;

/** 타임아웃 취소 아웃박스 복구 명령. */
public record RecoverTimeoutCancelOutboxCommand(int batchSize, long timeoutSeconds) {

    public Instant timeoutThreshold() {
        return Instant.now().minusSeconds(timeoutSeconds);
    }
}
