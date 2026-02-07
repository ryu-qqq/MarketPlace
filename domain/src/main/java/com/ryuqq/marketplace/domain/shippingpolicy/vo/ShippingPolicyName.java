package com.ryuqq.marketplace.domain.shippingpolicy.vo;

/** 배송 정책명 Value Object. */
public record ShippingPolicyName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    public ShippingPolicyName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("배송 정책명은 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("배송 정책명은 %d~%d자 이내여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static ShippingPolicyName of(String value) {
        return new ShippingPolicyName(value);
    }
}
