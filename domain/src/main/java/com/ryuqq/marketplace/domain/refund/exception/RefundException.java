package com.ryuqq.marketplace.domain.refund.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 환불 도메인 예외. */
public class RefundException extends DomainException {

    public RefundException(RefundErrorCode errorCode) {
        super(errorCode);
    }

    public RefundException(RefundErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public RefundException(RefundErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
