package com.ryuqq.marketplace.domain.shippingpolicy.exception;

/**
 * 마지막 활성 배송 정책 비활성화 시도 예외.
 *
 * <p>POL-DEACT-002: 최소 1개의 활성 정책이 필요합니다.
 */
public class LastActiveShippingPolicyCannotBeDeactivatedException extends ShippingPolicyException {

    private static final ShippingPolicyErrorCode ERROR_CODE =
            ShippingPolicyErrorCode.LAST_ACTIVE_POLICY_CANNOT_BE_DEACTIVATED;

    public LastActiveShippingPolicyCannotBeDeactivatedException() {
        super(ERROR_CODE);
    }
}
