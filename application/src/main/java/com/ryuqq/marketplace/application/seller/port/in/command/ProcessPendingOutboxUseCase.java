package com.ryuqq.marketplace.application.seller.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.seller.dto.command.ProcessPendingOutboxCommand;

/**
 * 대기 중인 셀러 인증 Outbox 처리 UseCase.
 *
 * <p>PENDING 상태이면서 재시도 횟수가 남아있는 Outbox를 처리합니다.
 */
public interface ProcessPendingOutboxUseCase {

    SchedulerBatchProcessingResult execute(ProcessPendingOutboxCommand command);
}
