package com.ryuqq.marketplace.domain.productintelligence.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** ProductIntelligence 도메인 에러 코드. */
public enum ProductIntelligenceErrorCode implements ErrorCode {

    // Profile (PI-001 ~ PI-099)
    PROFILE_NOT_FOUND("PI-001", 404, "상품 프로파일을 찾을 수 없습니다"),
    INVALID_PROFILE_STATE("PI-002", 409, "유효하지 않은 프로파일 상태 전환입니다"),
    ANALYSIS_ALREADY_COMPLETED("PI-003", 409, "이미 완료된 분석입니다"),
    ANALYSIS_NOT_ALL_COMPLETED("PI-004", 409, "모든 분석이 완료되지 않았습니다"),

    // Outbox (PI-100 ~ PI-199)
    INVALID_OUTBOX_STATE("PI-100", 409, "유효하지 않은 아웃박스 상태 전환입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ProductIntelligenceErrorCode(String code, int httpStatus, String message) {
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
