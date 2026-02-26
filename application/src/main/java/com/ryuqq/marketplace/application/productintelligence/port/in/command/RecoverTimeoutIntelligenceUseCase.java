package com.ryuqq.marketplace.application.productintelligence.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productintelligence.dto.command.RecoverTimeoutIntelligenceCommand;

/**
 * 타임아웃된 Intelligence Outbox 복구 UseCase.
 *
 * <p>SENT 상태에서 일정 시간 이상 경과한 좀비 상태 Outbox를 PENDING으로 복구합니다.
 */
public interface RecoverTimeoutIntelligenceUseCase {

    SchedulerBatchProcessingResult execute(RecoverTimeoutIntelligenceCommand command);
}
