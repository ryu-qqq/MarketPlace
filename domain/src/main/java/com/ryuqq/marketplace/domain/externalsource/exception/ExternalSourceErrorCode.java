package com.ryuqq.marketplace.domain.externalsource.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 외부 소스 도메인 에러 코드. */
public enum ExternalSourceErrorCode implements ErrorCode {
    EXTERNAL_SOURCE_NOT_FOUND("EXS-001", 404, "외부 소스를 찾을 수 없습니다"),
    EXTERNAL_SOURCE_CODE_DUPLICATE("EXS-002", 409, "이미 존재하는 외부 소스 코드입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ExternalSourceErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
