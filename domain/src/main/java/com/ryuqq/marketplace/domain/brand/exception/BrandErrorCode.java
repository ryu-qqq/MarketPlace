package com.ryuqq.marketplace.domain.brand.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 브랜드 도메인 에러 코드. */
public enum BrandErrorCode implements ErrorCode {

    // 브랜드 관련 (BRD-001 ~ BRD-099)
    BRAND_NOT_FOUND("BRD-001", 404, "브랜드를 찾을 수 없습니다"),
    BRAND_CODE_DUPLICATE("BRD-002", 409, "이미 존재하는 브랜드 코드입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    BrandErrorCode(String code, int httpStatus, String message) {
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
