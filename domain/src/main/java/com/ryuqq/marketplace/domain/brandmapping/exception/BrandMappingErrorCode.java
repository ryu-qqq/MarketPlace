package com.ryuqq.marketplace.domain.brandmapping.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** BrandMapping 도메인 에러 코드. */
public enum BrandMappingErrorCode implements ErrorCode {
    BRAND_MAPPING_NOT_FOUND("BRDMAP-001", 404, "브랜드 매핑을 찾을 수 없습니다"),
    BRAND_MAPPING_DUPLICATE("BRDMAP-002", 409, "해당 외부 브랜드에 이미 매핑이 존재합니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    BrandMappingErrorCode(String code, int httpStatus, String message) {
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
