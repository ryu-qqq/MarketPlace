package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;

/** 주문 생성 UseCase. */
public interface CreateOrderUseCase {

    String execute(CreateOrderCommand command);
}
