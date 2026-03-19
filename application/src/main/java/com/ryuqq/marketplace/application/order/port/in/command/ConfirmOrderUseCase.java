package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;

/** 주문상품 구매 확정 UseCase. */
public interface ConfirmOrderUseCase {

    void execute(OrderItemStatusCommand command);
}
