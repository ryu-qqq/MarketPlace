package com.ryuqq.marketplace.domain.refund.exception;

/** 환불 클레임을 찾을 수 없는 경우 예외. */
public class RefundNotFoundException extends RefundException {

    private static final RefundErrorCode ERROR_CODE = RefundErrorCode.REFUND_NOT_FOUND;

    public RefundNotFoundException() {
        super(ERROR_CODE);
    }

    public RefundNotFoundException(String refundClaimId) {
        super(ERROR_CODE, String.format("ID가 %s인 환불 클레임을 찾을 수 없습니다", refundClaimId));
    }
}
