package com.ryuqq.marketplace.application.productintelligence.dto.command;

/** Option 분석 실행 커맨드. SQS intelligence-option-analysis 큐에서 수신. */
public record ExecuteOptionAnalysisCommand(Long profileId, Long productGroupId) {

    public static ExecuteOptionAnalysisCommand of(Long profileId, Long productGroupId) {
        return new ExecuteOptionAnalysisCommand(profileId, productGroupId);
    }
}
