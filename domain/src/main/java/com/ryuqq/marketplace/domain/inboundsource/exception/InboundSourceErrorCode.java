package com.ryuqq.marketplace.domain.inboundsource.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 인바운드 소스 도메인 에러 코드. */
public enum InboundSourceErrorCode implements ErrorCode {
    INBOUND_SOURCE_NOT_FOUND("EXS-001", 404, "인바운드 소스를 찾을 수 없습니다"),
    INBOUND_SOURCE_CODE_DUPLICATE("EXS-002", 409, "이미 존재하는 인바운드 소스 코드입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    InboundSourceErrorCode(String code, int httpStatus, String message) {
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
