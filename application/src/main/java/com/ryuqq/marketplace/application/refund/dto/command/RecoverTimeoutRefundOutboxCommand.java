package com.ryuqq.marketplace.application.refund.dto.command;

import java.time.Instant;

/** 타임아웃 환불 아웃박스 복구 명령. */
public record RecoverTimeoutRefundOutboxCommand(int batchSize, long timeoutSeconds) {

    public Instant timeoutThreshold() {
        return Instant.now().minusSeconds(timeoutSeconds);
    }
}
