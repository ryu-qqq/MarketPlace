package com.ryuqq.marketplace.domain.order.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 주문 도메인 에러 코드. */
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND("ORD-001", 404, "주문을 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("ORD-002", 400, "유효하지 않은 주문 상태 변경입니다"),
    INVALID_ORDER_DATA("ORD-003", 400, "주문 데이터가 유효하지 않습니다"),
    ORDER_ALREADY_CANCELLED("ORD-004", 409, "이미 취소된 주문입니다"),
    ORDER_ALREADY_CONFIRMED("ORD-005", 409, "이미 구매 확정된 주문입니다"),
    EMPTY_ORDER_ITEMS("ORD-006", 400, "주문 상품은 최소 1개 이상이어야 합니다"),
    INVALID_CANCEL_QUANTITY("ORD-007", 400, "유효하지 않은 취소 수량입니다"),
    INVALID_RETURN_QUANTITY("ORD-008", 400, "유효하지 않은 반품 수량입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    OrderErrorCode(String code, int httpStatus, String message) {
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
