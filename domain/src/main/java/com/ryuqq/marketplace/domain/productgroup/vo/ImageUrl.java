package com.ryuqq.marketplace.domain.productgroup.vo;

/** 이미지 URL Value Object. */
public record ImageUrl(String value) {

    private static final int MAX_LENGTH = 500;

    public ImageUrl {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이미지 URL은 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("이미지 URL은 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static ImageUrl of(String value) {
        return new ImageUrl(value);
    }
}
