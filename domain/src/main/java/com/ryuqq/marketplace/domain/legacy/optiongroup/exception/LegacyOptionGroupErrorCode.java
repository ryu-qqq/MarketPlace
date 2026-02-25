package com.ryuqq.marketplace.domain.legacy.optiongroup.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 레거시 옵션 그룹 도메인 에러 코드. */
public enum LegacyOptionGroupErrorCode implements ErrorCode {
    LEGACY_OPTION_GROUP_NOT_FOUND("LGOPTGRP-001", 404, "레거시 옵션 그룹을 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    LegacyOptionGroupErrorCode(String code, int httpStatus, String message) {
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
