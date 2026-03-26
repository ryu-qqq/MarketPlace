package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;

/** 주문상품 발주확인(준비) UseCase. */
public interface PrepareOrderUseCase {

    void execute(OrderItemStatusCommand command);
}
