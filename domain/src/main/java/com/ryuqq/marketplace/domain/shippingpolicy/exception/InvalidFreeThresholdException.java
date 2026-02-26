package com.ryuqq.marketplace.domain.shippingpolicy.exception;

/**
 * 무료배송 기준금액이 유효하지 않은 경우 예외.
 *
 * <p>조건부 무료배송(CONDITIONAL_FREE) 설정 시 무료배송 기준금액이 필요합니다.
 */
public class InvalidFreeThresholdException extends ShippingPolicyException {

    private static final ShippingPolicyErrorCode ERROR_CODE =
            ShippingPolicyErrorCode.INVALID_FREE_THRESHOLD;

    public InvalidFreeThresholdException() {
        super(ERROR_CODE);
    }
}
