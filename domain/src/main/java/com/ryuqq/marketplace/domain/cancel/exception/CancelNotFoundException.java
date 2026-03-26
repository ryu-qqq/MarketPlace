package com.ryuqq.marketplace.domain.cancel.exception;

/** 취소를 찾을 수 없는 경우 예외. */
public class CancelNotFoundException extends CancelException {

    private static final CancelErrorCode ERROR_CODE = CancelErrorCode.CANCEL_NOT_FOUND;

    public CancelNotFoundException() {
        super(ERROR_CODE);
    }

    public CancelNotFoundException(String cancelId) {
        super(ERROR_CODE, String.format("ID가 %s인 취소를 찾을 수 없습니다", cancelId));
    }
}
