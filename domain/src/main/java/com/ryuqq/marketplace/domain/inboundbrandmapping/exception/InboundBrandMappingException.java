package com.ryuqq.marketplace.domain.inboundbrandmapping.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 외부 브랜드 매핑 도메인 예외. */
public class InboundBrandMappingException extends DomainException {

    public InboundBrandMappingException(InboundBrandMappingErrorCode errorCode) {
        super(errorCode);
    }

    public InboundBrandMappingException(
            InboundBrandMappingErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public InboundBrandMappingException(InboundBrandMappingErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
