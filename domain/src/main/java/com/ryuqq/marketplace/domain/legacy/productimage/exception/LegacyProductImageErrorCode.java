package com.ryuqq.marketplace.domain.legacy.productimage.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 레거시 상품 이미지 도메인 에러 코드. */
public enum LegacyProductImageErrorCode implements ErrorCode {
    LEGACY_PRODUCT_IMAGE_NOT_FOUND("LGPRDIMG-001", 404, "레거시 상품 이미지를 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    LegacyProductImageErrorCode(String code, int httpStatus, String message) {
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
