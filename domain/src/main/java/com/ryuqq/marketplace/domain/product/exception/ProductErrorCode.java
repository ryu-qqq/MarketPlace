package com.ryuqq.marketplace.domain.product.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** Product 도메인 에러 코드. */
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND("PRD-001", 404, "상품을 찾을 수 없습니다"),
    PRODUCT_INVALID_STATUS_TRANSITION("PRD-002", 400, "유효하지 않은 상태 전이입니다"),
    PRODUCT_INVALID_PRICE("PRD-003", 400, "할인가는 판매가보다 클 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ProductErrorCode(String code, int httpStatus, String message) {
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
