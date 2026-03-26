package com.ryuqq.marketplace.application.legacy.order.port.in.command;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderUpdateResult;

/** 레거시 주문 상태 변경 UseCase. */
public interface LegacyOrderUpdateUseCase {

    LegacyOrderUpdateResult execute(LegacyOrderUpdateCommand command);
}
