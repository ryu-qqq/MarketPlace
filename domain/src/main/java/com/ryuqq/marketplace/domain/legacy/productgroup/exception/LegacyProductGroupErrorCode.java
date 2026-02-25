package com.ryuqq.marketplace.domain.legacy.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 레거시 상품 그룹 도메인 에러 코드. */
public enum LegacyProductGroupErrorCode implements ErrorCode {
    LEGACY_PRODUCT_GROUP_NOT_FOUND("LGPRDGRP-001", 404, "레거시 상품 그룹을 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    LegacyProductGroupErrorCode(String code, int httpStatus, String message) {
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
