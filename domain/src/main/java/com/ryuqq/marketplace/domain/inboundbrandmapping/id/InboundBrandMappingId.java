package com.ryuqq.marketplace.domain.inboundbrandmapping.id;

/** InboundBrandMapping ID Value Object. */
public record InboundBrandMappingId(Long value) {

    public static InboundBrandMappingId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("InboundBrandMappingId 값은 null일 수 없습니다");
        }
        return new InboundBrandMappingId(value);
    }

    public static InboundBrandMappingId forNew() {
        return new InboundBrandMappingId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
