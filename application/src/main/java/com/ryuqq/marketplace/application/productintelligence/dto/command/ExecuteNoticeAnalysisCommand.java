package com.ryuqq.marketplace.application.productintelligence.dto.command;

/** Notice 분석 실행 커맨드. SQS intelligence-notice-analysis 큐에서 수신. */
public record ExecuteNoticeAnalysisCommand(Long profileId, Long productGroupId) {

    public static ExecuteNoticeAnalysisCommand of(Long profileId, Long productGroupId) {
        return new ExecuteNoticeAnalysisCommand(profileId, productGroupId);
    }
}
