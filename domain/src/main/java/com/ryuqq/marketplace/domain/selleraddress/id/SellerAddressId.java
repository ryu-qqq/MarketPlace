package com.ryuqq.marketplace.domain.selleraddress.id;

/** 셀러 주소 ID Value Object. */
public record SellerAddressId(Long value) {

    public static SellerAddressId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerAddressId 값은 null일 수 없습니다");
        }
        return new SellerAddressId(value);
    }

    public static SellerAddressId forNew() {
        return new SellerAddressId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
