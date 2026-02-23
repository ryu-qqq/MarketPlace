package com.ryuqq.marketplace.domain.outboundproduct.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** OutboundProduct 도메인 에러 코드. */
public enum OutboundProductErrorCode implements ErrorCode {
    OUTBOUND_PRODUCT_NOT_FOUND("OBP-001", 404, "아웃바운드 상품을 찾을 수 없습니다"),
    OUTBOUND_PRODUCT_ALREADY_REGISTERED("OBP-002", 409, "이미 등록된 아웃바운드 상품입니다"),
    OUTBOUND_PRODUCT_INVALID_STATUS("OBP-003", 400, "아웃바운드 상품 상태가 올바르지 않습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    OutboundProductErrorCode(String code, int httpStatus, String message) {
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
