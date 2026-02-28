package com.ryuqq.marketplace.application.outboundsync.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.outboundsync.dto.command.ProcessPendingOutboundSyncCommand;

/** PENDING 상태의 OutboundSync Outbox를 SQS로 발행하는 UseCase. */
public interface ProcessPendingOutboundSyncUseCase {

    SchedulerBatchProcessingResult execute(ProcessPendingOutboundSyncCommand command);
}
