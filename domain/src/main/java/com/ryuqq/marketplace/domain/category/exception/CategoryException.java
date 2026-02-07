package com.ryuqq.marketplace.domain.category.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 카테고리 도메인 예외. */
public class CategoryException extends DomainException {

    public CategoryException(CategoryErrorCode errorCode) {
        super(errorCode);
    }

    public CategoryException(CategoryErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public CategoryException(CategoryErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
