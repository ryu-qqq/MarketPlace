package com.ryuqq.marketplace.application.productintelligence.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productintelligence.dto.command.ProcessPendingIntelligenceCommand;

/**
 * 대기 중인 Intelligence Outbox 처리 UseCase.
 *
 * <p>PENDING 상태이면서 재시도 횟수가 남아있는 Outbox를 처리합니다.
 */
public interface ProcessPendingIntelligenceUseCase {

    SchedulerBatchProcessingResult execute(ProcessPendingIntelligenceCommand command);
}
