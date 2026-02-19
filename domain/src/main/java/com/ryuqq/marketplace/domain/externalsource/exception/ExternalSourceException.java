package com.ryuqq.marketplace.domain.externalsource.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 외부 소스 도메인 예외. */
public class ExternalSourceException extends DomainException {

    public ExternalSourceException(ExternalSourceErrorCode errorCode) {
        super(errorCode);
    }

    public ExternalSourceException(ExternalSourceErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ExternalSourceException(ExternalSourceErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
