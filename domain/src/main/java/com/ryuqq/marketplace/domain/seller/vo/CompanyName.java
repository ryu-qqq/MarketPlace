package com.ryuqq.marketplace.domain.seller.vo;

/** 상호명 Value Object. */
public record CompanyName(String value) {

    private static final int MAX_LENGTH = 100;

    public CompanyName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상호명은 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("상호명은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
    }

    public static CompanyName of(String value) {
        return new CompanyName(value);
    }
}
