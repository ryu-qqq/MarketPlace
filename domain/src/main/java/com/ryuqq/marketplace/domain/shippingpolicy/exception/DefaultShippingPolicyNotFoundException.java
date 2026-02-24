package com.ryuqq.marketplace.domain.shippingpolicy.exception;

/** 기본 배송 정책을 찾을 수 없을 때 예외. */
public class DefaultShippingPolicyNotFoundException extends ShippingPolicyException {

    public DefaultShippingPolicyNotFoundException(Long sellerId) {
        super(
                ShippingPolicyErrorCode.DEFAULT_SHIPPING_POLICY_NOT_FOUND,
                String.format("기본 배송 정책이 없습니다. 먼저 기본 배송 정책을 설정해 주세요 (sellerId: %d)", sellerId));
    }
}
