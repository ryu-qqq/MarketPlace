package com.ryuqq.marketplace.domain.cancel.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 취소 도메인 에러 코드. */
public enum CancelErrorCode implements ErrorCode {
    CANCEL_NOT_FOUND("CAN-001", 404, "취소를 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("CAN-002", 400, "유효하지 않은 취소 상태 변경입니다"),
    ALREADY_CANCELLED("CAN-003", 409, "이미 취소된 요청입니다"),
    INVALID_CANCEL_REASON("CAN-004", 400, "취소 사유가 유효하지 않습니다"),
    INVALID_CANCEL_QTY("CAN-005", 400, "취소 수량이 유효하지 않습니다"),
    ORDER_NOT_CANCELLABLE("CAN-006", 400, "취소할 수 없는 주문입니다"),
    EMPTY_CANCEL_ITEMS("CAN-007", 400, "취소 대상 상품은 최소 1개 이상이어야 합니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    CancelErrorCode(String code, int httpStatus, String message) {
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
