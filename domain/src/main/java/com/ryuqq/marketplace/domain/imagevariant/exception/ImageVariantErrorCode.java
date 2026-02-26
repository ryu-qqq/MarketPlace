package com.ryuqq.marketplace.domain.imagevariant.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** ImageVariant 도메인 에러 코드. */
public enum ImageVariantErrorCode implements ErrorCode {
    IMAGE_VARIANT_NOT_FOUND("IMGVAR-001", 404, "이미지 Variant를 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ImageVariantErrorCode(String code, int httpStatus, String message) {
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
