package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.ConfirmOrderCommand;

/** 주문 구매확정 UseCase. */
public interface ConfirmOrderUseCase {

    void execute(ConfirmOrderCommand command);
}
