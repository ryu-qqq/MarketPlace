package com.ryuqq.marketplace.domain.saleschannel.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 판매채널 에러 코드. */
public enum SalesChannelErrorCode implements ErrorCode {
    SALES_CHANNEL_NOT_FOUND("SCH-001", 404, "판매채널을 찾을 수 없습니다"),
    SALES_CHANNEL_NAME_DUPLICATE("SCH-002", 409, "이미 존재하는 판매채널명입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    SalesChannelErrorCode(String code, int httpStatus, String message) {
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
