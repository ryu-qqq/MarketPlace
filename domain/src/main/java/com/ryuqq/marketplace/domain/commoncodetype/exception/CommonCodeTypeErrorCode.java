package com.ryuqq.marketplace.domain.commoncodetype.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 공통 코드 타입 도메인 에러 코드. */
public enum CommonCodeTypeErrorCode implements ErrorCode {
    NOT_FOUND("CCT-001", 404, "공통 코드 타입을 찾을 수 없습니다"),
    DUPLICATE_CODE("CCT-002", 409, "동일한 코드가 이미 존재합니다"),
    DUPLICATE_DISPLAY_ORDER("CCT-003", 400, "동일한 표시 순서가 이미 존재합니다"),
    ACTIVE_COMMON_CODES_EXIST("CCT-004", 400, "활성화된 공통 코드가 존재하여 비활성화할 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    CommonCodeTypeErrorCode(String code, int httpStatus, String message) {
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
