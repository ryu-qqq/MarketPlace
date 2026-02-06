package com.ryuqq.marketplace.domain.seller.id;

/** 셀러 계약 ID Value Object. */
public record SellerContractId(Long value) {

    public static SellerContractId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerContractId 값은 null일 수 없습니다");
        }
        return new SellerContractId(value);
    }

    public static SellerContractId forNew() {
        return new SellerContractId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
