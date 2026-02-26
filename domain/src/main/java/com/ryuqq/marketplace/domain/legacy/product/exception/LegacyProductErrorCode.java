package com.ryuqq.marketplace.domain.legacy.product.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 레거시 상품 도메인 에러 코드. */
public enum LegacyProductErrorCode implements ErrorCode {
    LEGACY_PRODUCT_NOT_FOUND("LGPRD-001", 404, "레거시 상품을 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    LegacyProductErrorCode(String code, int httpStatus, String message) {
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
