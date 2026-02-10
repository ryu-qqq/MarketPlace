package com.ryuqq.marketplace.domain.saleschannelcategory.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

public enum SalesChannelCategoryErrorCode implements ErrorCode {
    SALES_CHANNEL_CATEGORY_NOT_FOUND("SCCAT-001", 404, "외부 채널 카테고리를 찾을 수 없습니다"),
    SALES_CHANNEL_CATEGORY_CODE_DUPLICATE("SCCAT-002", 409, "이미 존재하는 외부 카테고리 코드입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    SalesChannelCategoryErrorCode(String code, int httpStatus, String message) {
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
