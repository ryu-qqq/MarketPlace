package com.ryuqq.marketplace.domain.legacy.productimage.id;

/** 레거시(세토프) 상품 그룹 이미지 ID Value Object. */
public record LegacyProductImageId(Long value) {

    public static LegacyProductImageId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("LegacyProductImageId 값은 null일 수 없습니다");
        }
        return new LegacyProductImageId(value);
    }

    public static LegacyProductImageId forNew() {
        return new LegacyProductImageId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
