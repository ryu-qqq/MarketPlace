package com.ryuqq.marketplace.domain.brand.id;

/** 브랜드 ID Value Object. */
public record BrandId(Long value) {

    public static BrandId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("BrandId 값은 null일 수 없습니다");
        }
        return new BrandId(value);
    }

    public static BrandId forNew() {
        return new BrandId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
