package com.ryuqq.marketplace.domain.qna.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** QnA 도메인 예외. */
public class QnaException extends DomainException {

    public QnaException(QnaErrorCode errorCode) {
        super(errorCode);
    }

    public QnaException(QnaErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public QnaException(QnaErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
