package com.ryuqq.marketplace.application.productintelligence.dto.command;

import java.time.Instant;

/**
 * ANALYZING 상태에서 Aggregation 발행이 누락된 프로파일 복구 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 * @param stuckSeconds stuck 임계값 (초) - 이 시간 이상 ANALYZING + allCompleted 상태면 stuck으로 판단
 */
public record RecoverStuckAggregationCommand(int batchSize, long stuckSeconds) {

    public static RecoverStuckAggregationCommand of(int batchSize, long stuckSeconds) {
        return new RecoverStuckAggregationCommand(batchSize, stuckSeconds);
    }

    public Instant stuckThreshold() {
        return Instant.now().minusSeconds(stuckSeconds);
    }
}
