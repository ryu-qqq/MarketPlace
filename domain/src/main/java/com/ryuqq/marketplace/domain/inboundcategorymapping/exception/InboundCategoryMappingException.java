package com.ryuqq.marketplace.domain.inboundcategorymapping.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 외부 카테고리 매핑 도메인 예외. */
public class InboundCategoryMappingException extends DomainException {

    public InboundCategoryMappingException(InboundCategoryMappingErrorCode errorCode) {
        super(errorCode);
    }

    public InboundCategoryMappingException(
            InboundCategoryMappingErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public InboundCategoryMappingException(
            InboundCategoryMappingErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
