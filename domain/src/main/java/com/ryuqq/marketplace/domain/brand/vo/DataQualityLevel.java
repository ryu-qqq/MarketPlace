package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Data Quality Level Enum
 *
 * <p><strong>품질 수준</strong>:</p>
 * <ul>
 *   <li>UNKNOWN - 알 수 없음 (0점)</li>
 *   <li>LOW - 낮음 (1-39점)</li>
 *   <li>MID - 중간 (40-69점)</li>
 *   <li>HIGH - 높음 (70-100점)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum DataQualityLevel {
    UNKNOWN,
    LOW,
    MID,
    HIGH;

    /**
     * 문자열로부터 DataQualityLevel 찾기
     *
     * @param value 문자열 값
     * @return DataQualityLevel
     * @throws IllegalArgumentException 일치하는 Level이 없는 경우
     */
    public static DataQualityLevel fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DataQualityLevel 값은 null이거나 빈 문자열일 수 없습니다.");
        }

        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 DataQualityLevel 값입니다: " + value);
        }
    }
}
