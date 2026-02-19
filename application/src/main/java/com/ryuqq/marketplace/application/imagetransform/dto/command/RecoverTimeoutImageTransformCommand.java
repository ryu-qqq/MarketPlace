package com.ryuqq.marketplace.application.imagetransform.dto.command;

import java.time.Instant;

/**
 * 이미지 변환 Outbox 타임아웃 복구 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 * @param timeoutSeconds 타임아웃 기준 시간 (초)
 */
public record RecoverTimeoutImageTransformCommand(int batchSize, long timeoutSeconds) {

    public static RecoverTimeoutImageTransformCommand of(int batchSize, long timeoutSeconds) {
        return new RecoverTimeoutImageTransformCommand(batchSize, timeoutSeconds);
    }

    public Instant timeoutThreshold() {
        return Instant.now().minusSeconds(timeoutSeconds);
    }
}
