package com.ryuqq.marketplace.domain.refundpolicy.exception;

/**
 * 비활성 환불 정책을 기본으로 지정 시도 예외.
 *
 * <p>POL-DEACT-003: 비활성화된 정책은 기본으로 지정할 수 없습니다.
 */
public class InactiveRefundPolicyCannotBeDefaultException extends RefundPolicyException {

    private static final RefundPolicyErrorCode ERROR_CODE =
            RefundPolicyErrorCode.INACTIVE_POLICY_CANNOT_BE_DEFAULT;

    public InactiveRefundPolicyCannotBeDefaultException() {
        super(ERROR_CODE);
    }
}
