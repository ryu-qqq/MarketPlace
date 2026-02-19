package com.ryuqq.marketplace.domain.externalsource.exception;

/** 외부 소스 코드가 이미 존재하는 경우 예외. */
public class ExternalSourceDuplicateException extends ExternalSourceException {

    private static final ExternalSourceErrorCode ERROR_CODE =
            ExternalSourceErrorCode.EXTERNAL_SOURCE_CODE_DUPLICATE;

    public ExternalSourceDuplicateException(String code) {
        super(ERROR_CODE, String.format("외부 소스 코드 '%s'가 이미 존재합니다", code));
    }
}
