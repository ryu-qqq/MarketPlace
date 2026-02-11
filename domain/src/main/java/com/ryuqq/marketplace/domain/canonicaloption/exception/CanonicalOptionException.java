package com.ryuqq.marketplace.domain.canonicaloption.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 캐노니컬 옵션 도메인 예외. */
public class CanonicalOptionException extends DomainException {

    public CanonicalOptionException(CanonicalOptionErrorCode errorCode) {
        super(errorCode);
    }

    public CanonicalOptionException(CanonicalOptionErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public CanonicalOptionException(CanonicalOptionErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
