package com.ryuqq.marketplace.domain.refund.exception;

import java.util.List;

/** 환불 건의 소유권이 일치하지 않는 경우 예외. */
public class RefundOwnershipMismatchException extends RefundException {

    private static final RefundErrorCode ERROR_CODE = RefundErrorCode.REFUND_OWNERSHIP_MISMATCH;

    public RefundOwnershipMismatchException() {
        super(ERROR_CODE);
    }

    public RefundOwnershipMismatchException(List<String> missingIds) {
        super(ERROR_CODE, String.format("소유권 불일치 또는 존재하지 않는 환불 건: %s", missingIds));
    }
}
