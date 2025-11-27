package com.ryuqq.marketplace.domain.category.vo;

/**
 * Gender Scope Enum
 *
 * <p><strong>성별 구분</strong>:</p>
 * <ul>
 *   <li>MEN - 남성</li>
 *   <li>WOMEN - 여성</li>
 *   <li>UNISEX - 남녀공용</li>
 *   <li>KIDS - 아동</li>
 *   <li>NONE - 해당 없음</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum GenderScope {
    MEN("남성"),
    WOMEN("여성"),
    UNISEX("남녀공용"),
    KIDS("아동"),
    NONE("해당 없음");

    private final String displayName;

    GenderScope(String displayName) {
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
     * 문자열로부터 GenderScope 찾기
     *
     * @param value 문자열 값
     * @return GenderScope
     * @throws IllegalArgumentException 일치하는 GenderScope가 없는 경우
     */
    public static GenderScope fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("GenderScope 값은 null이거나 빈 문자열일 수 없습니다.");
        }

        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 GenderScope 값입니다: " + value);
        }
    }
}
