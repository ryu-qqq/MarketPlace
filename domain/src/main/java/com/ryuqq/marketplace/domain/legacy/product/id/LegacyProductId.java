package com.ryuqq.marketplace.domain.legacy.product.id;

/** 레거시(세토프) 상품 ID Value Object. */
public record LegacyProductId(Long value) {

    public static LegacyProductId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("LegacyProductId 값은 null일 수 없습니다");
        }
        return new LegacyProductId(value);
    }

    public static LegacyProductId forNew() {
        return new LegacyProductId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
