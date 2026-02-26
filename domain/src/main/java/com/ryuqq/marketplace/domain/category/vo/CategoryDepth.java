package com.ryuqq.marketplace.domain.category.vo;

/**
 * 카테고리 계층 깊이 Value Object.
 *
 * <p>루트 카테고리는 depth 0입니다.
 *
 * @param value 계층 깊이 (0 이상)
 */
public record CategoryDepth(int value) {

    private static final int MIN_DEPTH = 0;
    private static final int MAX_DEPTH = 10;

    public CategoryDepth {
        if (value < MIN_DEPTH || value > MAX_DEPTH) {
            throw new IllegalArgumentException(
                    String.format("카테고리 깊이는 %d~%d 범위여야 합니다", MIN_DEPTH, MAX_DEPTH));
        }
    }

    public static CategoryDepth of(int value) {
        return new CategoryDepth(value);
    }

    public static CategoryDepth root() {
        return new CategoryDepth(0);
    }

    /** 자식 카테고리 깊이 반환. */
    public CategoryDepth child() {
        return new CategoryDepth(value + 1);
    }

    public boolean isRoot() {
        return value == 0;
    }
}
