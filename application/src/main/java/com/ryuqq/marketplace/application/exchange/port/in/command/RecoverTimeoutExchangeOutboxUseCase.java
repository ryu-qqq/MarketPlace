package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.RecoverTimeoutExchangeOutboxCommand;

/** 타임아웃 교환 아웃박스 복구 UseCase. */
public interface RecoverTimeoutExchangeOutboxUseCase {
    SchedulerBatchProcessingResult execute(RecoverTimeoutExchangeOutboxCommand command);
}
