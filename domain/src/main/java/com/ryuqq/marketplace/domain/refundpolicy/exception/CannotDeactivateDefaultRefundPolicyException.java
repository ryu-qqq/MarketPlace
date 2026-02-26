package com.ryuqq.marketplace.domain.refundpolicy.exception;

/**
 * 기본 환불 정책 비활성화 시도 예외.
 *
 * <p>POL-DEACT-001: 기본 정책은 비활성화할 수 없습니다.
 */
public class CannotDeactivateDefaultRefundPolicyException extends RefundPolicyException {

    private static final RefundPolicyErrorCode ERROR_CODE =
            RefundPolicyErrorCode.CANNOT_DEACTIVATE_DEFAULT_POLICY;

    public CannotDeactivateDefaultRefundPolicyException() {
        super(ERROR_CODE);
    }
}
