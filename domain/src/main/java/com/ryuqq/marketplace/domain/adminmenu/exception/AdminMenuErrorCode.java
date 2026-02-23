package com.ryuqq.marketplace.domain.adminmenu.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** Admin 메뉴 도메인 에러 코드. */
public enum AdminMenuErrorCode implements ErrorCode {
    ADMIN_MENU_NOT_FOUND("ADMIN_MENU-001", 404, "관리자 메뉴를 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    AdminMenuErrorCode(String code, int httpStatus, String message) {
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
