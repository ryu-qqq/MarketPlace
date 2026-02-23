package com.ryuqq.marketplace.domain.inboundcategorymapping.id;

/** InboundCategoryMapping ID Value Object. */
public record InboundCategoryMappingId(Long value) {

    public static InboundCategoryMappingId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("InboundCategoryMappingId 값은 null일 수 없습니다");
        }
        return new InboundCategoryMappingId(value);
    }

    public static InboundCategoryMappingId forNew() {
        return new InboundCategoryMappingId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
