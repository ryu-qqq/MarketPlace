package com.ryuqq.marketplace.domain.saleschannelbrand.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

public class SalesChannelBrandException extends DomainException {

    public SalesChannelBrandException(SalesChannelBrandErrorCode errorCode) {
        super(errorCode);
    }

    public SalesChannelBrandException(SalesChannelBrandErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public SalesChannelBrandException(SalesChannelBrandErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
