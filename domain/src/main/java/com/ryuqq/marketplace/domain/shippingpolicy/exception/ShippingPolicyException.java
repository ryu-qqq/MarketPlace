package com.ryuqq.marketplace.domain.shippingpolicy.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 배송 정책 도메인 예외. */
public class ShippingPolicyException extends DomainException {

    public ShippingPolicyException(ShippingPolicyErrorCode errorCode) {
        super(errorCode);
    }

    public ShippingPolicyException(ShippingPolicyErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ShippingPolicyException(ShippingPolicyErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public static ShippingPolicyException policyNotFound() {
        return new ShippingPolicyException(ShippingPolicyErrorCode.SHIPPING_POLICY_NOT_FOUND);
    }

    public static ShippingPolicyException policyInactive() {
        return new ShippingPolicyException(ShippingPolicyErrorCode.SHIPPING_POLICY_INACTIVE);
    }

    public static ShippingPolicyException invalidFreeThreshold() {
        return new ShippingPolicyException(ShippingPolicyErrorCode.INVALID_FREE_THRESHOLD);
    }
}
