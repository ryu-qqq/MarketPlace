package com.ryuqq.marketplace.domain.productgroup.id;

/** 상세설명 이미지 ID Value Object. */
public record DescriptionImageId(Long value) {

    public static DescriptionImageId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("DescriptionImageId 값은 null일 수 없습니다");
        }
        return new DescriptionImageId(value);
    }

    public static DescriptionImageId forNew() {
        return new DescriptionImageId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
