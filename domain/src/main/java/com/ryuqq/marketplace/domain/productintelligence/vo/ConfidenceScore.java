package com.ryuqq.marketplace.domain.productintelligence.vo;

/**
 * 신뢰도 점수 Value Object.
 *
 * <p>0.0 ~ 1.0 범위의 AI 분석 신뢰도를 표현합니다.
 */
public record ConfidenceScore(double value) {

    /** 자동 적용 임계값. 이 이상이면 자동으로 보강 적용. */
    public static final double AUTO_APPLY_THRESHOLD = 0.95;

    /** 사람 검수 임계값. 이 이상이면 적용하되 사후 검수 대상. */
    public static final double REVIEW_THRESHOLD = 0.80;

    public ConfidenceScore {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("ConfidenceScore는 0.0~1.0 범위여야 합니다. 입력값: " + value);
        }
    }

    public static ConfidenceScore of(double value) {
        return new ConfidenceScore(value);
    }

    public static ConfidenceScore perfect() {
        return new ConfidenceScore(1.0);
    }

    public static ConfidenceScore zero() {
        return new ConfidenceScore(0.0);
    }

    /** 자동 적용 가능 여부 (confidence >= 0.95). */
    public boolean isAutoApplicable() {
        return value >= AUTO_APPLY_THRESHOLD;
    }

    /** 사람 검수 필요 여부 (0.80 <= confidence < 0.95). */
    public boolean needsReview() {
        return value >= REVIEW_THRESHOLD && value < AUTO_APPLY_THRESHOLD;
    }

    /** 수동 확인 필요 (confidence < 0.80). */
    public boolean needsManualConfirmation() {
        return value < REVIEW_THRESHOLD;
    }

    /** 정수 백분율 변환 (0~100). */
    public int toPercentage() {
        return (int) Math.round(value * 100);
    }
}
