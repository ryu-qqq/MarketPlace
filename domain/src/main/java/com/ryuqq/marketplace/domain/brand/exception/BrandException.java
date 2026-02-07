package com.ryuqq.marketplace.domain.brand.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 브랜드 도메인 예외. */
public class BrandException extends DomainException {

    public BrandException(BrandErrorCode errorCode) {
        super(errorCode);
    }

    public BrandException(BrandErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public BrandException(BrandErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
