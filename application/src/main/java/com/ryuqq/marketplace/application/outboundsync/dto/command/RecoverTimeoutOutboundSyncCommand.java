package com.ryuqq.marketplace.application.outboundsync.dto.command;

import java.time.Instant;

/**
 * PROCESSING 타임아웃 OutboundSync Outbox 복구 명령.
 *
 * @param batchSize 배치 크기
 * @param timeoutSeconds 타임아웃 기준 (PROCESSING 상태에서 N초 경과 시 복구)
 */
public record RecoverTimeoutOutboundSyncCommand(int batchSize, long timeoutSeconds) {

    public RecoverTimeoutOutboundSyncCommand {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be positive, got: " + batchSize);
        }
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException(
                    "timeoutSeconds must be positive, got: " + timeoutSeconds);
        }
    }

    public static RecoverTimeoutOutboundSyncCommand of(int batchSize, long timeoutSeconds) {
        return new RecoverTimeoutOutboundSyncCommand(batchSize, timeoutSeconds);
    }

    public Instant timeoutThreshold() {
        return Instant.now().minusSeconds(timeoutSeconds);
    }
}
