package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Confidence Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>범위: 0.0 ~ 1.0</li>
 *   <li>신뢰도 표현</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record Confidence(double value) {

    private static final double MIN_VALUE = 0.0;
    private static final double MAX_VALUE = 1.0;
    private static final double HIGH_CONFIDENCE_THRESHOLD = 0.7;

    /**
     * Compact Constructor (검증 로직)
     */
    public Confidence {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException(
                String.format("Confidence 값은 %.1f-%.1f 범위여야 합니다: %.2f", MIN_VALUE, MAX_VALUE, value)
            );
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 신뢰도 (0.0 ~ 1.0)
     * @return Confidence
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static Confidence of(double value) {
        return new Confidence(value);
    }

    /**
     * 확실한 신뢰도 생성 (1.0)
     *
     * @return Confidence (1.0)
     */
    public static Confidence certain() {
        return new Confidence(1.0);
    }

    /**
     * 불확실한 신뢰도 생성 (0.5)
     *
     * @return Confidence (0.5)
     */
    public static Confidence uncertain() {
        return new Confidence(0.5);
    }

    /**
     * 없음 (0.0)
     *
     * @return Confidence (0.0)
     */
    public static Confidence none() {
        return new Confidence(0.0);
    }

    /**
     * 높은 신뢰도 여부 확인
     *
     * @return 0.7 이상이면 true
     */
    public boolean isHighConfidence() {
        return value >= HIGH_CONFIDENCE_THRESHOLD;
    }

    /**
     * 낮은 신뢰도 여부 확인
     *
     * @return 0.5 미만이면 true
     */
    public boolean isLowConfidence() {
        return value < 0.5;
    }

    /**
     * 퍼센트로 변환
     *
     * @return 퍼센트 값 (0-100)
     */
    public int toPercent() {
        return (int) (value * 100);
    }
}
