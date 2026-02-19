package com.ryuqq.marketplace.domain.externalbrandmapping.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 외부 브랜드 매핑 도메인 에러 코드. */
public enum ExternalBrandMappingErrorCode implements ErrorCode {
    EXTERNAL_BRAND_MAPPING_NOT_FOUND("EBM-001", 404, "외부 브랜드 매핑을 찾을 수 없습니다"),
    EXTERNAL_BRAND_MAPPING_DUPLICATE("EBM-002", 409, "이미 존재하는 외부 브랜드 매핑입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ExternalBrandMappingErrorCode(String code, int httpStatus, String message) {
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
