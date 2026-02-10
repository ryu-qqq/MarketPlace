package com.ryuqq.marketplace.domain.brandpreset.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** BrandPreset 도메인 예외. */
public class BrandPresetException extends DomainException {

    public BrandPresetException(BrandPresetErrorCode errorCode) {
        super(errorCode);
    }

    public BrandPresetException(BrandPresetErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public BrandPresetException(BrandPresetErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
