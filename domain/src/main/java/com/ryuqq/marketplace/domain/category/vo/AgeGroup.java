package com.ryuqq.marketplace.domain.category.vo;

/**
 * Age Group Enum
 *
 * <p><strong>연령대 구분</strong>:</p>
 * <ul>
 *   <li>INFANT - 유아 (0-2세)</li>
 *   <li>KIDS - 어린이 (3-12세)</li>
 *   <li>TEEN - 청소년 (13-19세)</li>
 *   <li>ADULT - 성인 (20-64세)</li>
 *   <li>SENIOR - 노년 (65세 이상)</li>
 *   <li>NONE - 해당 없음</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum AgeGroup {
    INFANT("유아"),
    KIDS("어린이"),
    TEEN("청소년"),
    ADULT("성인"),
    SENIOR("노년"),
    NONE("해당 없음");

    private final String displayName;

    AgeGroup(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 표시용 이름 반환
     *
     * @return 한글 표시명
     */
    public String displayName() {
        return displayName;
    }

    /**
     * 문자열로부터 AgeGroup 찾기
     *
     * @param value 문자열 값
     * @return AgeGroup
     * @throws IllegalArgumentException 일치하는 AgeGroup이 없는 경우
     */
    public static AgeGroup fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AgeGroup 값은 null이거나 빈 문자열일 수 없습니다.");
        }

        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 AgeGroup 값입니다: " + value);
        }
    }
}
