package com.ryuqq.marketplace.domain.inboundcategorymapping.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 외부 카테고리 매핑 도메인 에러 코드. */
public enum InboundCategoryMappingErrorCode implements ErrorCode {
    EXTERNAL_CATEGORY_MAPPING_NOT_FOUND("ECM-001", 404, "외부 카테고리 매핑을 찾을 수 없습니다"),
    EXTERNAL_CATEGORY_MAPPING_DUPLICATE("ECM-002", 409, "이미 존재하는 외부 카테고리 매핑입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    InboundCategoryMappingErrorCode(String code, int httpStatus, String message) {
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
