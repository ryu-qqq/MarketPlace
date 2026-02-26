package com.ryuqq.marketplace.application.productintelligence.dto.command;

/** Aggregator 실행 커맨드. 마지막 Analyzer 완료 → Aggregation 큐에서 수신. */
public record AggregateAnalysisCommand(Long profileId, Long productGroupId) {

    public static AggregateAnalysisCommand of(Long profileId, Long productGroupId) {
        return new AggregateAnalysisCommand(profileId, productGroupId);
    }
}
