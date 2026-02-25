package com.ryuqq.marketplace.domain.legacy.optiondetail.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 레거시 옵션 상세 도메인 에러 코드. */
public enum LegacyOptionDetailErrorCode implements ErrorCode {
    LEGACY_OPTION_DETAIL_NOT_FOUND("LGOPTDTL-001", 404, "레거시 옵션 상세를 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    LegacyOptionDetailErrorCode(String code, int httpStatus, String message) {
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
