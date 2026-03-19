package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;

/** 주문상품 출고(배송 시작) UseCase. */
public interface ShipOrderUseCase {

    void execute(OrderItemStatusCommand command);
}
