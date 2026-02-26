package com.ryuqq.marketplace.domain.inboundsource.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 인바운드 소스 도메인 예외. */
public class InboundSourceException extends DomainException {

    public InboundSourceException(InboundSourceErrorCode errorCode) {
        super(errorCode);
    }

    public InboundSourceException(InboundSourceErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public InboundSourceException(InboundSourceErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
