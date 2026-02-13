package com.ryuqq.marketplace.domain.order.exception;

/** 주문을 찾을 수 없는 경우 예외. */
public class OrderNotFoundException extends OrderException {

    private static final OrderErrorCode ERROR_CODE = OrderErrorCode.ORDER_NOT_FOUND;

    public OrderNotFoundException() {
        super(ERROR_CODE);
    }

    public OrderNotFoundException(String orderId) {
        super(ERROR_CODE, String.format("ID가 %s인 주문을 찾을 수 없습니다", orderId));
    }
}
