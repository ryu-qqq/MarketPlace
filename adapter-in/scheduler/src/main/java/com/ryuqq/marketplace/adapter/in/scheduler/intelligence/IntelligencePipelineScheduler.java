package com.ryuqq.marketplace.adapter.in.scheduler.intelligence;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productintelligence.dto.command.ProcessPendingIntelligenceCommand;
import com.ryuqq.marketplace.application.productintelligence.dto.command.RecoverStuckAggregationCommand;
import com.ryuqq.marketplace.application.productintelligence.dto.command.RecoverTimeoutIntelligenceCommand;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.ProcessPendingIntelligenceUseCase;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.RecoverStuckAggregationUseCase;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.RecoverTimeoutIntelligenceUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Intelligence Pipeline Outbox Relay 스케줄러.
 *
 * <p>세 가지 작업을 수행합니다:
 *
 * <ul>
 *   <li>processPending (Outbox Relay): PENDING Outbox를 조회하여 3개 Analyzer 큐로 발행
 *   <li>recoverTimeout: SENT 상태에서 타임아웃된 Outbox를 PENDING으로 복구
 *   <li>recoverStuckAggregation: 분석 완료 후 Aggregation 발행이 누락된 프로파일 복구
 * </ul>
 *
 * @see SchedulerProperties
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.intelligence-pipeline.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class IntelligencePipelineScheduler {

    private final ProcessPendingIntelligenceUseCase processPendingUseCase;
    private final RecoverTimeoutIntelligenceUseCase recoverTimeoutUseCase;
    private final RecoverStuckAggregationUseCase recoverStuckAggregationUseCase;
    private final SchedulerProperties.IntelligencePipeline config;

    public IntelligencePipelineScheduler(
            ProcessPendingIntelligenceUseCase processPendingUseCase,
            RecoverTimeoutIntelligenceUseCase recoverTimeoutUseCase,
            RecoverStuckAggregationUseCase recoverStuckAggregationUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.recoverStuckAggregationUseCase = recoverStuckAggregationUseCase;
        this.config = schedulerProperties.jobs().intelligencePipeline();
    }

    /**
     * PENDING 상태의 Intelligence Outbox를 3개 Analyzer 큐로 발행합니다 (Outbox Relay).
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 조회하고 SQS로 발행합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.intelligence-pipeline.process-pending.cron}",
            zone = "${scheduler.jobs.intelligence-pipeline.process-pending.timezone}")
    @SchedulerJob("IntelligencePipeline-ProcessPending")
    public SchedulerBatchProcessingResult processPending() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingIntelligenceCommand command =
                ProcessPendingIntelligenceCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingUseCase.execute(command);
    }

    /**
     * SENT 상태에서 타임아웃된 좀비 Outbox를 복구합니다.
     *
     * <p>SENT 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다. 실제 재처리는 다음 processPending(Outbox
     * Relay) 주기에서 수행됩니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.intelligence-pipeline.recover-timeout.cron}",
            zone = "${scheduler.jobs.intelligence-pipeline.recover-timeout.timezone}")
    @SchedulerJob("IntelligencePipeline-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeout() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        RecoverTimeoutIntelligenceCommand command =
                RecoverTimeoutIntelligenceCommand.of(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutUseCase.execute(command);
    }

    /**
     * 분석 완료 후 Aggregation 큐 발행이 누락된 프로파일을 복구합니다.
     *
     * <p>ANALYZING 상태에서 모든 분석이 완료(completedCount >= expectedCount)되었지만 SQS 발행 실패 등으로 Aggregation
     * 단계로 전이되지 못한 프로파일을 찾아 Aggregation 큐를 재발행합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.intelligence-pipeline.recover-stuck-aggregation.cron}",
            zone = "${scheduler.jobs.intelligence-pipeline.recover-stuck-aggregation.timezone}")
    @SchedulerJob("IntelligencePipeline-RecoverStuckAggregation")
    public SchedulerBatchProcessingResult recoverStuckAggregation() {
        SchedulerProperties.RecoverStuckAggregation recoverStuck = config.recoverStuckAggregation();
        RecoverStuckAggregationCommand command =
                RecoverStuckAggregationCommand.of(
                        recoverStuck.batchSize(), recoverStuck.stuckSeconds());
        return recoverStuckAggregationUseCase.execute(command);
    }
}
