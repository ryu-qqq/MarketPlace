package com.ryuqq.marketplace.domain.seller.vo;

/** 노출명 (고객 노출용) Value Object. */
public record DisplayName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    public DisplayName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("노출명은 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("노출명은 %d~%d자 이내여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static DisplayName of(String value) {
        return new DisplayName(value);
    }
}
