package com.ryuqq.marketplace.domain.externalbrandmapping.id;

/** ExternalBrandMapping ID Value Object. */
public record ExternalBrandMappingId(Long value) {

    public static ExternalBrandMappingId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ExternalBrandMappingId 값은 null일 수 없습니다");
        }
        return new ExternalBrandMappingId(value);
    }

    public static ExternalBrandMappingId forNew() {
        return new ExternalBrandMappingId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
