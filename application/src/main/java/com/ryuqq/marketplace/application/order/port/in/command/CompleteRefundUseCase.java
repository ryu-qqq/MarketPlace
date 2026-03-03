package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.CompleteRefundCommand;

/** 환불 완료 UseCase. */
public interface CompleteRefundUseCase {

    void execute(CompleteRefundCommand command);
}
