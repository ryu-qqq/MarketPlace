package com.ryuqq.marketplace.domain.shippingpolicy.exception;

/**
 * 비활성 배송 정책을 기본으로 지정 시도 예외.
 *
 * <p>POL-DEACT-003: 비활성화된 정책은 기본으로 지정할 수 없습니다.
 */
public class InactiveShippingPolicyCannotBeDefaultException extends ShippingPolicyException {

    private static final ShippingPolicyErrorCode ERROR_CODE =
            ShippingPolicyErrorCode.INACTIVE_POLICY_CANNOT_BE_DEFAULT;

    public InactiveShippingPolicyCannotBeDefaultException() {
        super(ERROR_CODE);
    }
}
