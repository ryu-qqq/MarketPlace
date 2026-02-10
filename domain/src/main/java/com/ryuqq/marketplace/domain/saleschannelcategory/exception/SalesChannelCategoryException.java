package com.ryuqq.marketplace.domain.saleschannelcategory.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

public class SalesChannelCategoryException extends DomainException {

    public SalesChannelCategoryException(SalesChannelCategoryErrorCode errorCode) {
        super(errorCode);
    }

    public SalesChannelCategoryException(
            SalesChannelCategoryErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public SalesChannelCategoryException(SalesChannelCategoryErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
