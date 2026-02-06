package com.ryuqq.marketplace.application.selleradmin.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RecoverTimeoutSellerAdminOutboxCommand;

/**
 * 타임아웃된 셀러 관리자 인증 Outbox 복구 UseCase.
 *
 * <p>PROCESSING 상태에서 일정 시간 이상 경과한 좀비 상태 Outbox를 PENDING으로 복구합니다.
 */
public interface RecoverTimeoutSellerAdminOutboxUseCase {

    SchedulerBatchProcessingResult execute(RecoverTimeoutSellerAdminOutboxCommand command);
}
