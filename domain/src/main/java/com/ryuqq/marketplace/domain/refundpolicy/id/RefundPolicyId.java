package com.ryuqq.marketplace.domain.refundpolicy.id;

/** 환불 정책 ID Value Object. */
public record RefundPolicyId(Long value) {

    public static RefundPolicyId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("RefundPolicyId 값은 null일 수 없습니다");
        }
        return new RefundPolicyId(value);
    }

    public static RefundPolicyId forNew() {
        return new RefundPolicyId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
