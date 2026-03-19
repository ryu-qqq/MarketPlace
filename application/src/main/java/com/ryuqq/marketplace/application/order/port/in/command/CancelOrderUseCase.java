package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.OrderItemCancelCommand;

/** 주문상품 취소 UseCase. */
public interface CancelOrderUseCase {

    void execute(OrderItemCancelCommand command);
}
