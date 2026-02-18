package com.ryuqq.marketplace.adapter.in.scheduler.productgroupinspection;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ProcessPendingInspectionCommand;
import com.ryuqq.marketplace.application.productgroupinspection.dto.command.RecoverTimeoutInspectionCommand;
import com.ryuqq.marketplace.application.productgroupinspection.port.in.command.ProcessPendingInspectionUseCase;
import com.ryuqq.marketplace.application.productgroupinspection.port.in.command.RecoverTimeoutInspectionUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 상품 그룹 검수 Outbox Relay 스케줄러.
 *
 * <p>두 가지 작업을 수행합니다:
 *
 * <ul>
 *   <li>processPending (Outbox Relay): PENDING Outbox를 조회하여 SQS Scoring 큐로 발행
 *   <li>recoverTimeout: 진행 중(SENT/SCORING/ENHANCING/VERIFYING) 타임아웃 Outbox를 PENDING으로 복구
 * </ul>
 *
 * <p>실제 검수 로직은 SQS Consumer(Scoring → Enhancement → Verification)에서 비동기로 처리됩니다.
 *
 * @see SchedulerProperties
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.product-group-inspection.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ProductGroupInspectionScheduler {

    private final ProcessPendingInspectionUseCase processPendingUseCase;
    private final RecoverTimeoutInspectionUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.ProductGroupInspection config;

    public ProductGroupInspectionScheduler(
            ProcessPendingInspectionUseCase processPendingUseCase,
            RecoverTimeoutInspectionUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().productGroupInspection();
    }

    /**
     * PENDING 상태의 검수 Outbox를 SQS Scoring 큐로 발행합니다 (Outbox Relay).
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 조회하고, 선행 조건(이미지/상세설명 업로드 완료)을 확인한 후 SQS로 발행합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.product-group-inspection.process-pending.cron}",
            zone = "${scheduler.jobs.product-group-inspection.process-pending.timezone}")
    @SchedulerJob("ProductGroupInspection-ProcessPending")
    public SchedulerBatchProcessingResult processPending() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingInspectionCommand command =
                ProcessPendingInspectionCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingUseCase.execute(command);
    }

    /**
     * 진행 중 상태에서 타임아웃된 좀비 Outbox를 복구합니다.
     *
     * <p>SENT/SCORING/ENHANCING/VERIFYING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다. 실제 재처리는
     * 다음 processPending(Outbox Relay) 주기에서 수행됩니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.product-group-inspection.recover-timeout.cron}",
            zone = "${scheduler.jobs.product-group-inspection.recover-timeout.timezone}")
    @SchedulerJob("ProductGroupInspection-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeout() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        RecoverTimeoutInspectionCommand command =
                RecoverTimeoutInspectionCommand.of(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutUseCase.execute(command);
    }
}
