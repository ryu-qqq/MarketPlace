package com.ryuqq.marketplace.domain.shippingpolicy.exception;

/**
 * 기본 배송 정책 비활성화 시도 예외.
 *
 * <p>POL-DEACT-001: 기본 정책은 비활성화할 수 없습니다.
 */
public class CannotDeactivateDefaultShippingPolicyException extends ShippingPolicyException {

    private static final ShippingPolicyErrorCode ERROR_CODE =
            ShippingPolicyErrorCode.CANNOT_DEACTIVATE_DEFAULT_POLICY;

    public CannotDeactivateDefaultShippingPolicyException() {
        super(ERROR_CODE);
    }
}
