package com.ryuqq.marketplace.domain.cancel.exception;

import java.util.List;

/** 취소 건의 소유권이 일치하지 않는 경우 예외. */
public class CancelOwnershipMismatchException extends CancelException {

    private static final CancelErrorCode ERROR_CODE = CancelErrorCode.CANCEL_OWNERSHIP_MISMATCH;

    public CancelOwnershipMismatchException() {
        super(ERROR_CODE);
    }

    public CancelOwnershipMismatchException(List<String> missingIds) {
        super(ERROR_CODE, String.format("소유권 불일치 또는 존재하지 않는 취소 건: %s", missingIds));
    }
}
