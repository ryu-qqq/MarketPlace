package com.ryuqq.marketplace.domain.settlement.entry.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 정산 원장 에러 코드. */
public enum SettlementEntryErrorCode implements ErrorCode {
    ENTRY_NOT_FOUND("STE-001", 404, "정산 원장을 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("STE-002", 400, "유효하지 않은 상태 전이입니다"),
    ALREADY_SETTLED("STE-003", 409, "이미 정산 완료된 원장입니다"),
    ;

    private final String code;
    private final int httpStatus;
    private final String message;

    SettlementEntryErrorCode(String code, int httpStatus, String message) {
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
