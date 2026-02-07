package com.ryuqq.marketplace.domain.commoncode.vo;

/** 표시 순서 Value Object. */
public record DisplayOrder(int value) {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 9999;

    public DisplayOrder {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException(
                    String.format("표시 순서는 %d~%d 사이여야 합니다", MIN_VALUE, MAX_VALUE));
        }
    }

    public static DisplayOrder of(int value) {
        return new DisplayOrder(value);
    }

    public static DisplayOrder defaultOrder() {
        return new DisplayOrder(0);
    }
}
