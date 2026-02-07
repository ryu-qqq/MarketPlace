package com.ryuqq.marketplace.domain.commoncode.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 공통 코드 도메인 예외. */
public class CommonCodeException extends DomainException {

    public CommonCodeException(CommonCodeErrorCode errorCode) {
        super(errorCode);
    }

    public CommonCodeException(CommonCodeErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public CommonCodeException(CommonCodeErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
