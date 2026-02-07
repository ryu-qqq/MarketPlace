package com.ryuqq.marketplace.domain.seller.id;

/** 셀러 CS ID Value Object. */
public record SellerCsId(Long value) {

    public static SellerCsId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerCsId 값은 null일 수 없습니다");
        }
        return new SellerCsId(value);
    }

    public static SellerCsId forNew() {
        return new SellerCsId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
