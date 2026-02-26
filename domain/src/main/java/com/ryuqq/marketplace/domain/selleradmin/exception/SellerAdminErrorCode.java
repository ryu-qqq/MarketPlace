package com.ryuqq.marketplace.domain.selleradmin.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 셀러 관리자 도메인 에러 코드. */
public enum SellerAdminErrorCode implements ErrorCode {

    // 셀러 관리자 관련 (SELADM-001 ~ SELADM-099)
    SELLER_ADMIN_NOT_FOUND("SELADM-001", 404, "셀러 관리자를 찾을 수 없습니다"),
    SELLER_ADMIN_APPLICATION_NOT_FOUND("SELADM-002", 404, "셀러 관리자 가입 신청을 찾을 수 없습니다"),
    SELLER_ADMIN_ALREADY_PROCESSED("SELADM-003", 400, "이미 처리된 신청입니다"),
    SELLER_ADMIN_PENDING_EXISTS("SELADM-004", 409, "이미 대기 중인 신청이 존재합니다"),
    REJECTION_REASON_REQUIRED("SELADM-005", 400, "거절 사유는 필수입니다"),
    PASSWORD_RESET_NOT_ALLOWED("SELADM-006", 400, "비밀번호 초기화를 할 수 없는 상태입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    SellerAdminErrorCode(String code, int httpStatus, String message) {
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
