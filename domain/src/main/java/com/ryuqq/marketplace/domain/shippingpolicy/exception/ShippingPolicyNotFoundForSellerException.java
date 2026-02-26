package com.ryuqq.marketplace.domain.shippingpolicy.exception;

/** 해당 셀러의 배송 정책을 찾을 수 없을 때 예외. */
public class ShippingPolicyNotFoundForSellerException extends ShippingPolicyException {

    public ShippingPolicyNotFoundForSellerException(Long sellerId, Long shippingPolicyId) {
        super(
                ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND_FOR_SELLER,
                String.format(
                        "해당 셀러의 배송 정책을 찾을 수 없습니다 (sellerId: %d, shippingPolicyId: %d)",
                        sellerId, shippingPolicyId));
    }
}
