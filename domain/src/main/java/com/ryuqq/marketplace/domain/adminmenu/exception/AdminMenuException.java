package com.ryuqq.marketplace.domain.adminmenu.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** Admin 메뉴 도메인 예외. */
public class AdminMenuException extends DomainException {

    public AdminMenuException(AdminMenuErrorCode errorCode) {
        super(errorCode);
    }

    public AdminMenuException(AdminMenuErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public AdminMenuException(AdminMenuErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
