package com.ryuqq.marketplace.domain.inboundqna.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** InboundQna 도메인 예외. */
public class InboundQnaException extends DomainException {

    public InboundQnaException(InboundQnaErrorCode errorCode) {
        super(errorCode);
    }

    public InboundQnaException(InboundQnaErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public InboundQnaException(InboundQnaErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
