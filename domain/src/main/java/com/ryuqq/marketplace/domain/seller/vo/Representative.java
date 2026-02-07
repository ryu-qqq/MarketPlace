package com.ryuqq.marketplace.domain.seller.vo;

/** 대표자명 Value Object. */
public record Representative(String value) {

    private static final int MAX_LENGTH = 50;

    public Representative {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("대표자명은 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("대표자명은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
    }

    public static Representative of(String value) {
        return new Representative(value);
    }
}
