package com.ryuqq.marketplace.application.outboundsync.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.outboundsync.dto.command.RecoverTimeoutOutboundSyncCommand;

/** PROCESSING 타임아웃된 OutboundSync Outbox를 PENDING으로 복구하는 UseCase. */
public interface RecoverTimeoutOutboundSyncUseCase {

    SchedulerBatchProcessingResult execute(RecoverTimeoutOutboundSyncCommand command);
}
