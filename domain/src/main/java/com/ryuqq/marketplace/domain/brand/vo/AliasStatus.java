package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Alias Status Enum
 *
 * <p><strong>별칭 상태</strong>:</p>
 * <ul>
 *   <li>AUTO_SUGGESTED - 자동 제안</li>
 *   <li>PENDING_REVIEW - 검수 대기</li>
 *   <li>CONFIRMED - 확정</li>
 *   <li>REJECTED - 거부</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum AliasStatus {
    AUTO_SUGGESTED,
    PENDING_REVIEW,
    CONFIRMED,
    REJECTED;

    /**
     * 활성 상태 여부 확인
     *
     * @return CONFIRMED 또는 AUTO_SUGGESTED이면 true
     */
    public boolean isActive() {
        return this == CONFIRMED || this == AUTO_SUGGESTED;
    }

    /**
     * 검토 필요 여부 확인
     *
     * @return PENDING_REVIEW이면 true
     */
    public boolean needsReview() {
        return this == PENDING_REVIEW;
    }

    /**
     * 거부됨 여부 확인
     *
     * @return REJECTED이면 true
     */
    public boolean isRejected() {
        return this == REJECTED;
    }

    /**
     * 확정됨 여부 확인
     *
     * @return CONFIRMED이면 true
     */
    public boolean isConfirmed() {
        return this == CONFIRMED;
    }

    /**
     * 문자열로부터 AliasStatus 찾기
     *
     * @param value 문자열 값
     * @return AliasStatus
     * @throws IllegalArgumentException 일치하는 Status가 없는 경우
     */
    public static AliasStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AliasStatus 값은 null이거나 빈 문자열일 수 없습니다.");
        }

        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 AliasStatus 값입니다: " + value);
        }
    }
}
