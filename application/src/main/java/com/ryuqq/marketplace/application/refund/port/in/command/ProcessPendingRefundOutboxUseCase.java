package com.ryuqq.marketplace.application.refund.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.ProcessPendingRefundOutboxCommand;

/** PENDING 환불 아웃박스 처리 UseCase. */
public interface ProcessPendingRefundOutboxUseCase {
    SchedulerBatchProcessingResult execute(ProcessPendingRefundOutboxCommand command);
}
