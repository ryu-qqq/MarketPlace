package com.ryuqq.marketplace.domain.productgroup.id;

/** 셀러 옵션 값 ID Value Object. */
public record SellerOptionValueId(Long value) {

    public static SellerOptionValueId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerOptionValueId 값은 null일 수 없습니다");
        }
        return new SellerOptionValueId(value);
    }

    public static SellerOptionValueId forNew() {
        return new SellerOptionValueId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
