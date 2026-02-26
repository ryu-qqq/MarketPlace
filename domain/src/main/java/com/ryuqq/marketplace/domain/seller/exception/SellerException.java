package com.ryuqq.marketplace.domain.seller.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 셀러 도메인 예외. */
public class SellerException extends DomainException {

    public SellerException(SellerErrorCode errorCode) {
        super(errorCode);
    }

    public SellerException(SellerErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public SellerException(SellerErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
