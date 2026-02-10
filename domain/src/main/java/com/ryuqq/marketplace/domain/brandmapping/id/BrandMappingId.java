package com.ryuqq.marketplace.domain.brandmapping.id;

/** BrandMapping ID Value Object. */
public record BrandMappingId(Long value) {

    public static BrandMappingId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("BrandMappingId 값은 null일 수 없습니다");
        }
        return new BrandMappingId(value);
    }

    public static BrandMappingId forNew() {
        return new BrandMappingId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
