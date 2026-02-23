package com.ryuqq.marketplace.application.productintelligence.dto.command;

/** Description 분석 실행 커맨드. SQS intelligence-description-analysis 큐에서 수신. */
public record ExecuteDescriptionAnalysisCommand(Long profileId, Long productGroupId) {

    public static ExecuteDescriptionAnalysisCommand of(Long profileId, Long productGroupId) {
        return new ExecuteDescriptionAnalysisCommand(profileId, productGroupId);
    }
}
