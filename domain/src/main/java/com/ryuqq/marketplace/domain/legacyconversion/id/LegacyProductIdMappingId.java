package com.ryuqq.marketplace.domain.legacyconversion.id;

/**
 * 레거시 상품 ID 매핑 ID Value Object.
 *
 * <p>레거시 상품그룹과 내부 상품그룹 간의 매핑 레코드를 식별하는 ID입니다.
 */
public record LegacyProductIdMappingId(Long value) {

    public static LegacyProductIdMappingId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("LegacyProductIdMappingId 값은 null일 수 없습니다");
        }
        return new LegacyProductIdMappingId(value);
    }

    public static LegacyProductIdMappingId forNew() {
        return new LegacyProductIdMappingId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
