package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Brand Status Enum
 *
 * <p><strong>브랜드 상태</strong>:</p>
 * <ul>
 *   <li>ACTIVE - 활성</li>
 *   <li>INACTIVE - 비활성</li>
 *   <li>BLOCKED - 차단</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum BrandStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    /**
     * 사용 가능 여부 확인
     *
     * @return 활성 상태이면 true
     */
    public boolean isUsable() {
        return this == ACTIVE;
    }

    /**
     * 차단 여부 확인
     *
     * @return 차단 상태이면 true
     */
    public boolean isBlocked() {
        return this == BLOCKED;
    }

    /**
     * 문자열로부터 BrandStatus 찾기
     *
     * @param value 문자열 값
     * @return BrandStatus
     * @throws IllegalArgumentException 일치하는 BrandStatus가 없는 경우
     */
    public static BrandStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BrandStatus 값은 null이거나 빈 문자열일 수 없습니다.");
        }

        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 BrandStatus 값입니다: " + value);
        }
    }
}
