package com.ryuqq.marketplace.domain.externalbrandmapping.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 외부 브랜드 매핑 도메인 예외. */
public class ExternalBrandMappingException extends DomainException {

    public ExternalBrandMappingException(ExternalBrandMappingErrorCode errorCode) {
        super(errorCode);
    }

    public ExternalBrandMappingException(
            ExternalBrandMappingErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ExternalBrandMappingException(ExternalBrandMappingErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
