package com.ryuqq.marketplace.application.refund.dto.command;

import java.time.Instant;

/** PENDING 상태 환불 아웃박스 처리 명령. */
public record ProcessPendingRefundOutboxCommand(int batchSize, int delaySeconds) {

    public Instant beforeTime() {
        return Instant.now().minusSeconds(delaySeconds);
    }
}
