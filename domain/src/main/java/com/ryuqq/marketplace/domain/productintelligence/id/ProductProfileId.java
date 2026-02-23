package com.ryuqq.marketplace.domain.productintelligence.id;

/** 상품 프로파일 ID Value Object. */
public record ProductProfileId(Long value) {

    public static ProductProfileId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ProductProfileId 값은 null일 수 없습니다");
        }
        return new ProductProfileId(value);
    }

    public static ProductProfileId forNew() {
        return new ProductProfileId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
