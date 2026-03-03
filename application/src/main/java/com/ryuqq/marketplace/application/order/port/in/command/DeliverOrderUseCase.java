package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.DeliverOrderCommand;

/** 주문 배송완료 UseCase. */
public interface DeliverOrderUseCase {

    void execute(DeliverOrderCommand command);
}
