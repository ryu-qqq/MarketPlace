package com.ryuqq.marketplace.domain.saleschannel.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 판매채널 도메인 예외. */
public class SalesChannelException extends DomainException {

    public SalesChannelException(SalesChannelErrorCode errorCode) {
        super(errorCode);
    }

    public SalesChannelException(SalesChannelErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public SalesChannelException(SalesChannelErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
