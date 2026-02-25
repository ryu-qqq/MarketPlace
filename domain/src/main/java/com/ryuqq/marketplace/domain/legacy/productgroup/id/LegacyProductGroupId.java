package com.ryuqq.marketplace.domain.legacy.productgroup.id;

/** 레거시(세토프) 상품 그룹 ID Value Object. */
public record LegacyProductGroupId(Long value) {

    public static LegacyProductGroupId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("LegacyProductGroupId 값은 null일 수 없습니다");
        }
        return new LegacyProductGroupId(value);
    }

    public static LegacyProductGroupId forNew() {
        return new LegacyProductGroupId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
