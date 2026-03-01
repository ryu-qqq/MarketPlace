package com.ryuqq.marketplace.application.inboundproduct.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;

public interface RetryPendingMappingUseCase {
    SchedulerBatchProcessingResult execute(int batchSize);
}
