package com.ryuqq.marketplace.domain.catalog.category.vo;

/**
 * Category Status Enum
 *
 * <p><strong>카테고리 상태</strong>:</p>
 * <ul>
 *   <li>ACTIVE - 활성 (사용 가능)</li>
 *   <li>INACTIVE - 비활성 (임시 중단)</li>
 *   <li>DEPRECATED - 폐기 (더 이상 사용하지 않음)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum CategoryStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    DEPRECATED("폐기");

    private final String displayName;

    CategoryStatus(String displayName) {
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
     * 사용 가능한 상태인지 확인
     *
     * @return ACTIVE이면 true
     */
    public boolean isUsable() {
        return this == ACTIVE;
    }

    /**
     * 표시 가능한 상태인지 확인
     *
     * @return ACTIVE이면 true
     */
    public boolean isVisible() {
        return this == ACTIVE;
    }

    /**
     * 문자열로부터 Enum 변환
     *
     * @param value 문자열 값
     * @return CategoryStatus
     * @throws IllegalArgumentException 유효하지 않은 값인 경우
     */
    public static CategoryStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CategoryStatus value cannot be null or blank");
        }
        try {
            return CategoryStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CategoryStatus: " + value);
        }
    }
}
