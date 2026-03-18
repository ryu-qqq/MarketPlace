package com.ryuqq.marketplace.domain.legacyconversion.id;

/** 레거시 주문 ID 매핑 ID Value Object. */
public record LegacyOrderIdMappingId(Long value) {

    public static LegacyOrderIdMappingId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("LegacyOrderIdMappingId 값은 null일 수 없습니다");
        }
        return new LegacyOrderIdMappingId(value);
    }

    public static LegacyOrderIdMappingId forNew() {
        return new LegacyOrderIdMappingId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
