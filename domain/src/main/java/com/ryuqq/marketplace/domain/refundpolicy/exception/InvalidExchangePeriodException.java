package com.ryuqq.marketplace.domain.refundpolicy.exception;

/**
 * 교환 기간이 유효하지 않은 경우 예외.
 *
 * <p>교환 기간은 1~90일 이내여야 합니다.
 */
public class InvalidExchangePeriodException extends RefundPolicyException {

    private static final RefundPolicyErrorCode ERROR_CODE =
            RefundPolicyErrorCode.INVALID_EXCHANGE_PERIOD;

    public InvalidExchangePeriodException() {
        super(ERROR_CODE);
    }
}
