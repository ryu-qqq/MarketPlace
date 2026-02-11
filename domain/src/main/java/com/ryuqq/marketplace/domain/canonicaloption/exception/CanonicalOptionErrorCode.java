package com.ryuqq.marketplace.domain.canonicaloption.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 캐노니컬 옵션 도메인 에러 코드. */
public enum CanonicalOptionErrorCode implements ErrorCode {
    CANONICAL_OPTION_GROUP_NOT_FOUND("CANONICAL_OPTION-001", 404, "캐노니컬 옵션 그룹을 찾을 수 없습니다"),
    CANONICAL_OPTION_VALUE_NOT_FOUND("CANONICAL_OPTION-002", 404, "캐노니컬 옵션 값을 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    CanonicalOptionErrorCode(String code, int httpStatus, String message) {
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
