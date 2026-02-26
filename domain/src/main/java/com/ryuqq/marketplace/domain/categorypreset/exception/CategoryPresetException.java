package com.ryuqq.marketplace.domain.categorypreset.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** CategoryPreset 도메인 예외. */
public class CategoryPresetException extends DomainException {

    public CategoryPresetException(CategoryPresetErrorCode errorCode) {
        super(errorCode);
    }

    public CategoryPresetException(CategoryPresetErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public CategoryPresetException(CategoryPresetErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
