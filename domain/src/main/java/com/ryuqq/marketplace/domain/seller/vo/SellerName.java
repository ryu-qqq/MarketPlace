package com.ryuqq.marketplace.domain.seller.vo;

/** 셀러명 (내부 관리용) Value Object. */
public record SellerName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    public SellerName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("셀러명은 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("셀러명은 %d~%d자 이내여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static SellerName of(String value) {
        return new SellerName(value);
    }
}
