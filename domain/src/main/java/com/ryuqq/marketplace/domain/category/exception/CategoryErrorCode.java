package com.ryuqq.marketplace.domain.category.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 카테고리 도메인 에러 코드. */
public enum CategoryErrorCode implements ErrorCode {
    CATEGORY_NOT_FOUND("CAT-001", 404, "카테고리를 찾을 수 없습니다"),
    CATEGORY_CODE_DUPLICATE("CAT-002", 409, "이미 존재하는 카테고리 코드입니다"),
    CATEGORY_DEPTH_EXCEEDED("CAT-004", 400, "카테고리 최대 깊이를 초과하였습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    CategoryErrorCode(String code, int httpStatus, String message) {
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
