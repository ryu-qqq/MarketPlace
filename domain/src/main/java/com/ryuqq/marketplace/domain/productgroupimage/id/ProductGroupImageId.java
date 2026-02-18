package com.ryuqq.marketplace.domain.productgroupimage.id;

/** 상품 그룹 이미지 ID Value Object. */
public record ProductGroupImageId(Long value) {

    public static ProductGroupImageId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ProductGroupImageId 값은 null일 수 없습니다");
        }
        return new ProductGroupImageId(value);
    }

    public static ProductGroupImageId forNew() {
        return new ProductGroupImageId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
