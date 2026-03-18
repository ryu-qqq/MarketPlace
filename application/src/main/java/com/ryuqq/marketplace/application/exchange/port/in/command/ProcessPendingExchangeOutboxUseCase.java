package com.ryuqq.marketplace.application.exchange.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ProcessPendingExchangeOutboxCommand;

/** PENDING 교환 아웃박스 처리 UseCase. */
public interface ProcessPendingExchangeOutboxUseCase {
    SchedulerBatchProcessingResult execute(ProcessPendingExchangeOutboxCommand command);
}
