package com.ryuqq.marketplace.domain.refundpolicy.vo;

/** 환불 정책명 Value Object. */
public record RefundPolicyName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    public RefundPolicyName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("환불 정책명은 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("환불 정책명은 %d~%d자 이내여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static RefundPolicyName of(String value) {
        return new RefundPolicyName(value);
    }
}
