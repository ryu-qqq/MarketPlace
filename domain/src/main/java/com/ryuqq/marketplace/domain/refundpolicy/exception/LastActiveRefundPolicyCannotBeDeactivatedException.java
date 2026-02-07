package com.ryuqq.marketplace.domain.refundpolicy.exception;

/**
 * 마지막 활성 환불 정책 비활성화 시도 예외.
 *
 * <p>POL-DEACT-002: 최소 1개의 활성 정책이 필요합니다.
 */
public class LastActiveRefundPolicyCannotBeDeactivatedException extends RefundPolicyException {

    private static final RefundPolicyErrorCode ERROR_CODE =
            RefundPolicyErrorCode.LAST_ACTIVE_POLICY_CANNOT_BE_DEACTIVATED;

    public LastActiveRefundPolicyCannotBeDeactivatedException() {
        super(ERROR_CODE);
    }
}
