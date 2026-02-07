package com.ryuqq.marketplace.domain.shippingpolicy.id;

/** 배송 정책 ID Value Object. */
public record ShippingPolicyId(Long value) {

    public static ShippingPolicyId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ShippingPolicyId 값은 null일 수 없습니다");
        }
        return new ShippingPolicyId(value);
    }

    public static ShippingPolicyId forNew() {
        return new ShippingPolicyId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
