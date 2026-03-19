package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;

/** 교환 완료 처리 UseCase. */
public interface CompleteExchangeUseCase {

    void execute(OrderItemStatusCommand command);
}
