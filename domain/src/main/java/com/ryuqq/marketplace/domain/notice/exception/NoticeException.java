package com.ryuqq.marketplace.domain.notice.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 고시정보 도메인 예외. */
public class NoticeException extends DomainException {

    public NoticeException(NoticeErrorCode errorCode) {
        super(errorCode);
    }

    public NoticeException(NoticeErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public NoticeException(NoticeErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
