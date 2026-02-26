package com.ryuqq.marketplace.domain.commoncode.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 공통 코드 도메인 에러 코드. */
public enum CommonCodeErrorCode implements ErrorCode {
    COMMON_CODE_NOT_FOUND("CMC-001", 404, "공통 코드를 찾을 수 없습니다"),
    COMMON_CODE_DUPLICATE("CMC-002", 409, "동일한 타입과 코드가 이미 존재합니다"),
    COMMON_CODE_INACTIVE("CMC-003", 400, "비활성화된 공통 코드입니다"),
    INVALID_COMMON_CODE_TYPE("CMC-004", 400, "유효하지 않은 공통 코드 타입입니다"),
    INVALID_DISPLAY_ORDER("CMC-005", 400, "표시 순서는 0 이상이어야 합니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    CommonCodeErrorCode(String code, int httpStatus, String message) {
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
