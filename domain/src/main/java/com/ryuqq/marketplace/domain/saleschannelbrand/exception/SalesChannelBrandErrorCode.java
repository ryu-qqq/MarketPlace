package com.ryuqq.marketplace.domain.saleschannelbrand.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

public enum SalesChannelBrandErrorCode implements ErrorCode {
    SALES_CHANNEL_BRAND_NOT_FOUND("SCBRD-001", 404, "외부 채널 브랜드를 찾을 수 없습니다"),
    SALES_CHANNEL_BRAND_CODE_DUPLICATE("SCBRD-002", 409, "이미 존재하는 외부 브랜드 코드입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    SalesChannelBrandErrorCode(String code, int httpStatus, String message) {
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
