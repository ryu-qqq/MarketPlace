package com.ryuqq.marketplace.domain.productnotice.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** ProductNotice 도메인 에러 코드. */
public enum ProductNoticeErrorCode implements ErrorCode {
    PRODUCT_NOTICE_NOT_FOUND("PRDNTC-001", 404, "상품 고시정보를 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ProductNoticeErrorCode(String code, int httpStatus, String message) {
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
