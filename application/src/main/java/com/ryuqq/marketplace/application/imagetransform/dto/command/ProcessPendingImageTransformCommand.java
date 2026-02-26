package com.ryuqq.marketplace.application.imagetransform.dto.command;

import java.time.Instant;

/**
 * PENDING 이미지 변환 Outbox 처리 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 * @param delaySeconds 생성 후 최소 대기 시간 (초)
 */
public record ProcessPendingImageTransformCommand(int batchSize, int delaySeconds) {

    public static ProcessPendingImageTransformCommand of(int batchSize, int delaySeconds) {
        return new ProcessPendingImageTransformCommand(batchSize, delaySeconds);
    }

    public Instant beforeTime() {
        return Instant.now().minusSeconds(delaySeconds);
    }
}
