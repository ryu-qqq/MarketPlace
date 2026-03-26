package com.ryuqq.marketplace.application.refund.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.RecoverTimeoutRefundOutboxCommand;

/** 타임아웃 환불 아웃박스 복구 UseCase. */
public interface RecoverTimeoutRefundOutboxUseCase {
    SchedulerBatchProcessingResult execute(RecoverTimeoutRefundOutboxCommand command);
}
