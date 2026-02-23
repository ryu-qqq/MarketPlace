package com.ryuqq.marketplace.domain.outboundproduct.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** OutboundProduct 도메인 예외. */
public class OutboundProductException extends DomainException {

    public OutboundProductException(OutboundProductErrorCode errorCode) {
        super(errorCode);
    }

    public OutboundProductException(OutboundProductErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public OutboundProductException(OutboundProductErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
