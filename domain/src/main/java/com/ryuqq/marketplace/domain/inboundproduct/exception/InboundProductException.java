package com.ryuqq.marketplace.domain.inboundproduct.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** InboundProduct 도메인 예외. */
public class InboundProductException extends DomainException {

    public InboundProductException(InboundProductErrorCode errorCode) {
        super(errorCode);
    }

    public InboundProductException(InboundProductErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public InboundProductException(InboundProductErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
