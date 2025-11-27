package com.ryuqq.marketplace.domain.category.vo;

/**
 * Sort Order Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>0 이상</li>
 *   <li>작은 숫자가 앞에 표시</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record SortOrder(int value) {

    /**
     * Compact Constructor (검증 로직)
     */
    public SortOrder {
        if (value < 0) {
            throw new IllegalArgumentException("SortOrder는 음수일 수 없습니다: " + value);
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 정렬 순서
     * @return SortOrder
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static SortOrder of(int value) {
        return new SortOrder(value);
    }

    /**
     * 기본 정렬 순서 반환
     *
     * @return SortOrder (0)
     */
    public static SortOrder defaultOrder() {
        return new SortOrder(0);
    }
}
