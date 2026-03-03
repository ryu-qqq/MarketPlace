package com.ryuqq.marketplace.application.order.port.in.command;

import com.ryuqq.marketplace.application.order.dto.command.CompleteExchangeCommand;

/** 교환 완료 UseCase. */
public interface CompleteExchangeUseCase {

    void execute(CompleteExchangeCommand command);
}
