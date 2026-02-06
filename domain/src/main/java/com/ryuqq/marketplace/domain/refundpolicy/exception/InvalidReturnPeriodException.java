package com.ryuqq.marketplace.domain.refundpolicy.exception;

/**
 * 반품 기간이 유효하지 않은 경우 예외.
 *
 * <p>반품 기간은 1~90일 이내여야 합니다.
 */
public class InvalidReturnPeriodException extends RefundPolicyException {

    private static final RefundPolicyErrorCode ERROR_CODE =
            RefundPolicyErrorCode.INVALID_RETURN_PERIOD;

    public InvalidReturnPeriodException() {
        super(ERROR_CODE);
    }
}
