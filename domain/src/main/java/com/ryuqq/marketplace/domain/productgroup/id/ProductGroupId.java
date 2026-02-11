package com.ryuqq.marketplace.domain.productgroup.id;

/** 상품 그룹 ID Value Object. */
public record ProductGroupId(Long value) {

    public static ProductGroupId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ProductGroupId 값은 null일 수 없습니다");
        }
        return new ProductGroupId(value);
    }

    public static ProductGroupId forNew() {
        return new ProductGroupId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
