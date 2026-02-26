package com.ryuqq.marketplace.domain.brandmapping.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** BrandMapping 도메인 예외. */
public class BrandMappingException extends DomainException {

    public BrandMappingException(BrandMappingErrorCode errorCode) {
        super(errorCode);
    }

    public BrandMappingException(BrandMappingErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public BrandMappingException(BrandMappingErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
