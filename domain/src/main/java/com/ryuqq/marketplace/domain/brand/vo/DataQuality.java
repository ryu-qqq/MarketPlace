package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Data Quality Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>score: 0-100 범위</li>
 *   <li>level: 품질 수준</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record DataQuality(DataQualityLevel level, int score) {

    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 100;

    /**
     * Compact Constructor (검증 로직)
     */
    public DataQuality {
        if (level == null) {
            throw new IllegalArgumentException("DataQualityLevel은 null일 수 없습니다.");
        }

        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new IllegalArgumentException(
                String.format("score는 %d-%d 범위여야 합니다: %d", MIN_SCORE, MAX_SCORE, score)
            );
        }
    }

    /**
     * 값 기반 생성
     *
     * @param level 품질 수준
     * @param score 점수 (0-100)
     * @return DataQuality
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static DataQuality of(DataQualityLevel level, int score) {
        return new DataQuality(level, score);
    }

    /**
     * 알 수 없는 품질 생성
     *
     * @return UNKNOWN 수준, 0점
     */
    public static DataQuality unknown() {
        return new DataQuality(DataQualityLevel.UNKNOWN, 0);
    }

    /**
     * 점수 기반 자동 레벨 계산
     *
     * @param score 점수 (0-100)
     * @return DataQuality
     */
    public static DataQuality fromScore(int score) {
        DataQualityLevel level;
        if (score == 0) {
            level = DataQualityLevel.UNKNOWN;
        } else if (score < 40) {
            level = DataQualityLevel.LOW;
        } else if (score < 70) {
            level = DataQualityLevel.MID;
        } else {
            level = DataQualityLevel.HIGH;
        }
        return new DataQuality(level, score);
    }

    /**
     * 높은 품질 여부 확인
     *
     * @return HIGH 수준이면 true
     */
    public boolean isHighQuality() {
        return level == DataQualityLevel.HIGH;
    }

    /**
     * 낮은 품질 여부 확인
     *
     * @return LOW 또는 UNKNOWN이면 true
     */
    public boolean isLowQuality() {
        return level == DataQualityLevel.LOW || level == DataQualityLevel.UNKNOWN;
    }
}
