package com.ryuqq.marketplace.application.selleradmin.dto.command;

import java.time.Instant;

/**
 * 타임아웃 셀러 관리자 이메일 Outbox 복구 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 * @param timeoutSeconds 타임아웃 임계값 (초) - 이 시간 이상 PROCESSING 상태면 좀비로 판단
 */
public record RecoverTimeoutEmailOutboxCommand(int batchSize, long timeoutSeconds) {

    public static RecoverTimeoutEmailOutboxCommand of(int batchSize, long timeoutSeconds) {
        return new RecoverTimeoutEmailOutboxCommand(batchSize, timeoutSeconds);
    }

    public Instant timeoutThreshold() {
        return Instant.now().minusSeconds(timeoutSeconds);
    }
}
