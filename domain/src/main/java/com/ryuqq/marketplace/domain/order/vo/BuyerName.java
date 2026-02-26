package com.ryuqq.marketplace.domain.order.vo;

/** 구매자 이름 Value Object. */
public record BuyerName(String value) {

    public BuyerName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("구매자 이름은 필수입니다");
        }
    }

    public static BuyerName of(String value) {
        return new BuyerName(value);
    }
}
