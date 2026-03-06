package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.ShipOrderCommand;

/** 주문 발송 UseCase. */
public interface ShipOrderUseCase {

    void execute(ShipOrderCommand command);
}
