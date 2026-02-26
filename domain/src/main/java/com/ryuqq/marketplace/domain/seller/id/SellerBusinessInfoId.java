package com.ryuqq.marketplace.domain.seller.id;

/** 셀러 사업자 정보 ID Value Object. */
public record SellerBusinessInfoId(Long value) {

    public static SellerBusinessInfoId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerBusinessInfoId 값은 null일 수 없습니다");
        }
        return new SellerBusinessInfoId(value);
    }

    public static SellerBusinessInfoId forNew() {
        return new SellerBusinessInfoId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
