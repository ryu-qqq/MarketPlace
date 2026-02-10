package com.ryuqq.marketplace.domain.categorymapping.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** CategoryMapping 도메인 예외. */
public class CategoryMappingException extends DomainException {

    public CategoryMappingException(CategoryMappingErrorCode errorCode) {
        super(errorCode);
    }

    public CategoryMappingException(CategoryMappingErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public CategoryMappingException(CategoryMappingErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
