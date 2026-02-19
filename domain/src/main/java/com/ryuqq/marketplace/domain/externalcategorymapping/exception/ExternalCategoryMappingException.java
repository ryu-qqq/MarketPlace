package com.ryuqq.marketplace.domain.externalcategorymapping.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 외부 카테고리 매핑 도메인 예외. */
public class ExternalCategoryMappingException extends DomainException {

    public ExternalCategoryMappingException(ExternalCategoryMappingErrorCode errorCode) {
        super(errorCode);
    }

    public ExternalCategoryMappingException(
            ExternalCategoryMappingErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ExternalCategoryMappingException(
            ExternalCategoryMappingErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
