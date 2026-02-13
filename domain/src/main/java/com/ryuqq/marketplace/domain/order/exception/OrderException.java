package com.ryuqq.marketplace.domain.order.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 주문 도메인 예외. */
public class OrderException extends DomainException {

    public OrderException(OrderErrorCode errorCode) {
        super(errorCode);
    }

    public OrderException(OrderErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public OrderException(OrderErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
