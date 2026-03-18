package com.ryuqq.marketplace.domain.refund.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 환불 도메인 에러 코드. */
public enum RefundErrorCode implements ErrorCode {
    REFUND_NOT_FOUND("RFD-001", 404, "환불 클레임을 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("RFD-002", 400, "유효하지 않은 환불 상태 변경입니다"),
    ALREADY_COMPLETED("RFD-003", 409, "이미 완료된 환불입니다"),
    HOLD_REASON_REQUIRED("RFD-004", 400, "보류 사유는 필수입니다"),
    NOT_HOLD_STATUS("RFD-005", 400, "보류 상태가 아닙니다"),
    ALREADY_HOLD("RFD-006", 409, "이미 보류 상태입니다"),
    INVALID_REFUND_QTY("RFD-007", 400, "환불 수량은 1 이상이어야 합니다"),
    REASON_UPDATE_NOT_ALLOWED("RFD-008", 400, "현재 상태에서는 사유를 변경할 수 없습니다"),
    REFUND_OWNERSHIP_MISMATCH("RFD-009", 403, "환불 클레임 소유권이 일치하지 않습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    RefundErrorCode(String code, int httpStatus, String message) {
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
