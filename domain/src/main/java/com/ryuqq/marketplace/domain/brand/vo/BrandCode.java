package com.ryuqq.marketplace.domain.brand.vo;

/** 브랜드 고유 코드 Value Object. */
public record BrandCode(String value) {

    private static final int MAX_LENGTH = 100;

    public BrandCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("브랜드 코드는 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("브랜드 코드는 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static BrandCode of(String value) {
        return new BrandCode(value);
    }
}
