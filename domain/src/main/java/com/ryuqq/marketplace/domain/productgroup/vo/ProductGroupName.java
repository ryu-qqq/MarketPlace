package com.ryuqq.marketplace.domain.productgroup.vo;

/** 상품 그룹명 Value Object. */
public record ProductGroupName(String value) {

    private static final int MAX_LENGTH = 200;

    public ProductGroupName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 그룹명은 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("상품 그룹명은 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static ProductGroupName of(String value) {
        return new ProductGroupName(value);
    }
}
