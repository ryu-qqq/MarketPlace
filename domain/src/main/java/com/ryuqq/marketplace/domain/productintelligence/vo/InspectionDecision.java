package com.ryuqq.marketplace.domain.productintelligence.vo;

import java.time.Instant;
import java.util.List;

/**
 * 검수 최종 판정 결과 Value Object.
 *
 * <p>Aggregator가 모든 분석 결과를 종합하여 내린 최종 판정을 표현합니다.
 */
public record InspectionDecision(
        DecisionType decisionType,
        ConfidenceScore overallConfidence,
        List<String> reasons,
        Instant decidedAt) {

    public InspectionDecision {
        if (decisionType == null) {
            throw new IllegalArgumentException("InspectionDecision decisionType은 필수입니다");
        }
        if (overallConfidence == null) {
            throw new IllegalArgumentException("InspectionDecision overallConfidence는 필수입니다");
        }
        reasons = reasons != null ? List.copyOf(reasons) : List.of();
    }

    public static InspectionDecision autoApprove(
            double confidence, List<String> reasons, Instant now) {
        return new InspectionDecision(
                DecisionType.AUTO_APPROVED, ConfidenceScore.of(confidence), reasons, now);
    }

    public static InspectionDecision humanReview(
            double confidence, List<String> reasons, Instant now) {
        return new InspectionDecision(
                DecisionType.HUMAN_REVIEW, ConfidenceScore.of(confidence), reasons, now);
    }

    public static InspectionDecision autoReject(
            double confidence, List<String> reasons, Instant now) {
        return new InspectionDecision(
                DecisionType.AUTO_REJECTED, ConfidenceScore.of(confidence), reasons, now);
    }

    public boolean isApproved() {
        return decisionType.isApproved();
    }

    public boolean needsReview() {
        return decisionType.needsReview();
    }

    public boolean isRejected() {
        return decisionType.isRejected();
    }

    public int overallConfidencePercentage() {
        return overallConfidence.toPercentage();
    }
}
