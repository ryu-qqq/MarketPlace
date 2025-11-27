package com.ryuqq.marketplace.domain.category.vo;

/**
 * Category Depth Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>최소 0 (루트)</li>
 *   <li>최대 10</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CategoryDepth(int value) {

    private static final int MAX_DEPTH = 10;

    /**
     * Compact Constructor (검증 로직)
     */
    public CategoryDepth {
        if (value < 0) {
            throw new IllegalArgumentException("CategoryDepth는 음수일 수 없습니다: " + value);
        }
        if (value > MAX_DEPTH) {
            throw new IllegalArgumentException("CategoryDepth는 " + MAX_DEPTH + "를 초과할 수 없습니다: " + value);
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 깊이 값
     * @return CategoryDepth
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static CategoryDepth of(int value) {
        return new CategoryDepth(value);
    }

    /**
     * 깊이 증가
     *
     * @return 증가된 CategoryDepth
     * @throws IllegalArgumentException 최대 깊이 초과 시
     */
    public CategoryDepth increment() {
        return new CategoryDepth(value + 1);
    }

    /**
     * 루트 깊이인지 확인
     *
     * @return 루트이면 true
     */
    public boolean isRoot() {
        return value == 0;
    }
}
