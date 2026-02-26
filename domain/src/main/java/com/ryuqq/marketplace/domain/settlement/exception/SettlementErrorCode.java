package com.ryuqq.marketplace.domain.settlement.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 정산 도메인 에러 코드. */
public enum SettlementErrorCode implements ErrorCode {
    SETTLEMENT_NOT_FOUND("STL-001", 404, "정산을 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("STL-002", 400, "유효하지 않은 정산 상태 변경입니다"),
    NOT_PENDING_STATUS("STL-003", 400, "대기 상태가 아닙니다"),
    NOT_HOLD_STATUS("STL-004", 400, "보류 상태가 아닙니다"),
    ALREADY_HOLD("STL-005", 409, "이미 보류된 정산입니다"),
    HOLD_REASON_REQUIRED("STL-006", 400, "보류 사유는 필수입니다"),
    HAS_HOLD_SETTLEMENT("STL-007", 400, "보류 중인 정산이 존재합니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    SettlementErrorCode(String code, int httpStatus, String message) {
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
