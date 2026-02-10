package com.ryuqq.marketplace.domain.categorymapping.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** CategoryMapping 도메인 에러 코드. */
public enum CategoryMappingErrorCode implements ErrorCode {
    CATEGORY_MAPPING_NOT_FOUND("CATMAP-001", 404, "카테고리 매핑을 찾을 수 없습니다"),
    CATEGORY_MAPPING_DUPLICATE("CATMAP-002", 409, "해당 외부 카테고리에 이미 매핑이 존재합니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    CategoryMappingErrorCode(String code, int httpStatus, String message) {
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
