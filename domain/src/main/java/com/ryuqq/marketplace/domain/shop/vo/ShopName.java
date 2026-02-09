package com.ryuqq.marketplace.domain.shop.vo;

/** 외부몰명 Value Object. */
public record ShopName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    public ShopName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("외부몰명은 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("외부몰명은 %d~%d자 이내여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static ShopName of(String value) {
        return new ShopName(value);
    }
}
