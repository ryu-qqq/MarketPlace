package com.ryuqq.marketplace.domain.exchange.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 교환 도메인 에러 코드. */
public enum ExchangeErrorCode implements ErrorCode {
    EXCHANGE_NOT_FOUND("EXC-001", 404, "교환 클레임을 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("EXC-002", 400, "유효하지 않은 교환 상태 변경입니다"),
    ALREADY_COMPLETED("EXC-003", 409, "이미 완료된 교환입니다"),
    SAME_OPTION_SELECTED("EXC-004", 400, "동일한 옵션으로 교환할 수 없습니다"),
    OPTION_NOT_AVAILABLE("EXC-005", 400, "선택한 옵션은 이용할 수 없습니다"),
    LINKED_ORDER_REQUIRED("EXC-006", 400, "연결 주문이 필요합니다"),
    INVALID_EXCHANGE_QTY("EXC-007", 400, "교환 수량이 유효하지 않습니다"),
    TARGET_UPDATE_NOT_ALLOWED("EXC-008", 400, "현재 상태에서는 교환 대상을 변경할 수 없습니다"),
    REASON_UPDATE_NOT_ALLOWED("EXC-009", 400, "현재 상태에서는 사유를 변경할 수 없습니다"),
    EXCHANGE_OWNERSHIP_MISMATCH("EXC-010", 403, "요청한 교환 건의 소유권이 일치하지 않습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ExchangeErrorCode(String code, int httpStatus, String message) {
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
