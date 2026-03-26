package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;

/** 환불 완료 처리 UseCase. */
public interface CompleteRefundUseCase {

    void execute(OrderItemStatusCommand command);
}
