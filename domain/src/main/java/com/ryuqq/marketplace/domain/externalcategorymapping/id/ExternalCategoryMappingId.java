package com.ryuqq.marketplace.domain.externalcategorymapping.id;

/** ExternalCategoryMapping ID Value Object. */
public record ExternalCategoryMappingId(Long value) {

    public static ExternalCategoryMappingId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ExternalCategoryMappingId 값은 null일 수 없습니다");
        }
        return new ExternalCategoryMappingId(value);
    }

    public static ExternalCategoryMappingId forNew() {
        return new ExternalCategoryMappingId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
