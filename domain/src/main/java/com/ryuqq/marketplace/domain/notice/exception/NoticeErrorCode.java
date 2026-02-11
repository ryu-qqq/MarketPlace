package com.ryuqq.marketplace.domain.notice.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 고시정보 도메인 에러 코드. */
public enum NoticeErrorCode implements ErrorCode {
    NOTICE_CATEGORY_NOT_FOUND("NOTICE-001", 404, "고시정보 카테고리를 찾을 수 없습니다"),
    NOTICE_FIELD_NOT_FOUND("NOTICE-002", 404, "고시정보 필드를 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    NoticeErrorCode(String code, int httpStatus, String message) {
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
