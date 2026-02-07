package com.ryuqq.marketplace.domain.sellerapplication.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 셀러 입점 신청 도메인 예외. */
public class SellerApplicationException extends DomainException {

    public SellerApplicationException(SellerApplicationErrorCode errorCode) {
        super(errorCode);
    }

    public SellerApplicationException(SellerApplicationErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public SellerApplicationException(SellerApplicationErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
