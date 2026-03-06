package com.ryuqq.marketplace.application.inboundorder.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;

/** PENDING_MAPPING 상태 인바운드 주문 재시도 UseCase. */
public interface RetryInboundOrderMappingUseCase {

    SchedulerBatchProcessingResult execute(int batchSize);
}
