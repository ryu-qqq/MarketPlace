package com.ryuqq.marketplace.domain.inboundorder.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** InboundOrder 도메인 예외. */
public class InboundOrderException extends DomainException {

    public InboundOrderException(InboundOrderErrorCode errorCode) {
        super(errorCode);
    }

    public InboundOrderException(InboundOrderErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public InboundOrderException(InboundOrderErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
