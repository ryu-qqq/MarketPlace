package com.ryuqq.marketplace.domain.category.vo;

/** 카테고리 고유 코드 Value Object. */
public record CategoryCode(String value) {

    private static final int MAX_LENGTH = 100;

    public CategoryCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("카테고리 코드는 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("카테고리 코드는 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static CategoryCode of(String value) {
        return new CategoryCode(value);
    }
}
