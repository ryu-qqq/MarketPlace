package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Department Enum
 *
 * <p><strong>브랜드 카테고리</strong>:</p>
 * <ul>
 *   <li>FASHION - 패션</li>
 *   <li>BEAUTY - 뷰티</li>
 *   <li>LIVING - 리빙</li>
 *   <li>DIGITAL - 디지털</li>
 *   <li>ETC - 기타</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum Department {
    FASHION("패션"),
    BEAUTY("뷰티"),
    LIVING("리빙"),
    DIGITAL("디지털"),
    ETC("기타");

    private final String displayName;

    Department(String displayName) {
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
     * 문자열로부터 Department 찾기
     *
     * @param value 문자열 값
     * @return Department
     * @throws IllegalArgumentException 일치하는 Department가 없는 경우
     */
    public static Department fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Department 값은 null이거나 빈 문자열일 수 없습니다.");
        }

        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 Department 값입니다: " + value);
        }
    }
}
