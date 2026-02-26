package com.ryuqq.marketplace.application.productintelligence.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productintelligence.dto.command.RecoverStuckAggregationCommand;

/**
 * ANALYZING 상태에서 Aggregation 큐 발행이 누락된 프로파일 복구 UseCase.
 *
 * <p>모든 분석이 완료되었지만(completedCount >= expectedCount) SQS 발행 실패 등으로 ANALYZING 상태에 머물러 있는 프로파일을 찾아
 * Aggregation 큐를 재발행합니다.
 */
public interface RecoverStuckAggregationUseCase {

    SchedulerBatchProcessingResult execute(RecoverStuckAggregationCommand command);
}
