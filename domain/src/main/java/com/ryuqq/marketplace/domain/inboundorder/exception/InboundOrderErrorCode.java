package com.ryuqq.marketplace.domain.inboundorder.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** InboundOrder 도메인 에러 코드. */
public enum InboundOrderErrorCode implements ErrorCode {
    INBOUND_ORDER_NOT_FOUND("IBO-001", 404, "인바운드 주문을 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("IBO-002", 400, "유효하지 않은 인바운드 주문 상태 변경입니다"),
    DUPLICATE_EXTERNAL_ORDER("IBO-003", 409, "이미 수신된 외부 주문입니다"),
    MAPPING_FAILED("IBO-004", 400, "주문 상품 매핑에 실패했습니다"),
    CONVERSION_FAILED("IBO-005", 500, "내부 주문 변환에 실패했습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    InboundOrderErrorCode(String code, int httpStatus, String message) {
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
