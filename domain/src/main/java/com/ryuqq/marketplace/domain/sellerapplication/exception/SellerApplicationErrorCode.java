package com.ryuqq.marketplace.domain.sellerapplication.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 셀러 입점 신청 도메인 에러 코드. */
public enum SellerApplicationErrorCode implements ErrorCode {

    // 입점 신청 관련 (SELAPP-001 ~ SELAPP-099)
    SELLER_APPLICATION_NOT_FOUND("SELAPP-001", 404, "입점 신청을 찾을 수 없습니다"),
    SELLER_APPLICATION_ALREADY_PROCESSED("SELAPP-002", 400, "이미 처리된 신청입니다"),
    SELLER_APPLICATION_PENDING_EXISTS("SELAPP-003", 409, "이미 대기 중인 신청이 존재합니다"),
    REJECTION_REASON_REQUIRED("SELAPP-004", 400, "거절 사유는 필수입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    SellerApplicationErrorCode(String code, int httpStatus, String message) {
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
