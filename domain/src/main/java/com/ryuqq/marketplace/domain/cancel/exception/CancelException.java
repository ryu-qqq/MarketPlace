package com.ryuqq.marketplace.domain.cancel.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 취소 도메인 예외. */
public class CancelException extends DomainException {

    public CancelException(CancelErrorCode errorCode) {
        super(errorCode);
    }

    public CancelException(CancelErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public CancelException(CancelErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
