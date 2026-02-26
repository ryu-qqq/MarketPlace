package com.ryuqq.marketplace.domain.sellerapplication.id;

/** 셀러 입점 신청 ID Value Object. */
public record SellerApplicationId(Long value) {

    public static SellerApplicationId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerApplicationId 값은 null일 수 없습니다");
        }
        return new SellerApplicationId(value);
    }

    public static SellerApplicationId forNew() {
        return new SellerApplicationId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
