package com.ryuqq.marketplace.domain.category.vo;

/**
 * 카테고리 정렬 순서 Value Object.
 *
 * @param value 정렬 순서 (0 이상)
 */
public record SortOrder(int value) {

    public SortOrder {
        if (value < 0) {
            throw new IllegalArgumentException("정렬 순서는 0 이상이어야 합니다");
        }
    }

    public static SortOrder of(int value) {
        return new SortOrder(value);
    }

    public static SortOrder defaultOrder() {
        return new SortOrder(0);
    }
}
