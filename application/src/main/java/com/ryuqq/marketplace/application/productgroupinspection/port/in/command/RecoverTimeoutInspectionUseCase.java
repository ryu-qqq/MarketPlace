package com.ryuqq.marketplace.application.productgroupinspection.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productgroupinspection.dto.command.RecoverTimeoutInspectionCommand;

/**
 * 타임아웃된 검수 Outbox 복구 UseCase.
 *
 * <p>PROCESSING 상태에서 일정 시간 이상 경과한 좀비 상태 Outbox를 PENDING으로 복구합니다.
 */
public interface RecoverTimeoutInspectionUseCase {

    SchedulerBatchProcessingResult execute(RecoverTimeoutInspectionCommand command);
}
