package com.ryuqq.marketplace.domain.shop.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** Shop 도메인 예외. */
public class ShopException extends DomainException {

    public ShopException(ShopErrorCode errorCode) {
        super(errorCode);
    }

    public ShopException(ShopErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ShopException(ShopErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
