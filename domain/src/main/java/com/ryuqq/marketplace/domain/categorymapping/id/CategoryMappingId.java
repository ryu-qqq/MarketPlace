package com.ryuqq.marketplace.domain.categorymapping.id;

/** CategoryMapping ID Value Object. */
public record CategoryMappingId(Long value) {

    public static CategoryMappingId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CategoryMappingId 값은 null일 수 없습니다");
        }
        return new CategoryMappingId(value);
    }

    public static CategoryMappingId forNew() {
        return new CategoryMappingId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
