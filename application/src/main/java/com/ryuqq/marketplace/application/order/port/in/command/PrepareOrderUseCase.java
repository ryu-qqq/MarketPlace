package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.PrepareOrderCommand;

/** 주문 발주확인 UseCase. */
public interface PrepareOrderUseCase {

    void execute(PrepareOrderCommand command);
}
