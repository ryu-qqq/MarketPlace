package com.ryuqq.marketplace.application.selleradmin.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ProcessPendingEmailOutboxCommand;

/**
 * 대기 중인 셀러 관리자 이메일 Outbox 처리 UseCase.
 *
 * <p>PENDING 상태이면서 재시도 횟수가 남아있는 이메일 Outbox를 처리합니다.
 */
public interface ProcessPendingEmailOutboxUseCase {

    SchedulerBatchProcessingResult execute(ProcessPendingEmailOutboxCommand command);
}
