package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;

/** 주문상품 배송완료 UseCase. */
public interface DeliverOrderUseCase {

    void execute(OrderItemStatusCommand command);
}
