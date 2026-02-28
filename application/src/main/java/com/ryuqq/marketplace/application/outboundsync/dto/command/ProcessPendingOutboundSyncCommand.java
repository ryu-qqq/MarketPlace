package com.ryuqq.marketplace.application.outboundsync.dto.command;

import java.time.Instant;

/**
 * PENDING 상태의 OutboundSync Outbox 처리 명령.
 *
 * @param batchSize 배치 크기
 * @param delaySeconds 처리 지연 (생성 후 N초 경과한 건만 처리)
 */
public record ProcessPendingOutboundSyncCommand(int batchSize, int delaySeconds) {

    public static ProcessPendingOutboundSyncCommand of(int batchSize, int delaySeconds) {
        return new ProcessPendingOutboundSyncCommand(batchSize, delaySeconds);
    }

    public Instant beforeTime() {
        return Instant.now().minusSeconds(delaySeconds);
    }
}
