package com.ryuqq.marketplace.adapter.in.scheduler.claim;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.cancel.dto.command.ProcessPendingCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.RecoverTimeoutCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.port.in.command.ProcessPendingCancelOutboxUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.command.RecoverTimeoutCancelOutboxUseCase;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 취소 아웃박스 처리 스케줄러.
 *
 * <p>두 가지 작업을 수행합니다:
 *
 * <ul>
 *   <li>processPending: PENDING Outbox를 조회하여 SQS로 발행
 *   <li>recoverTimeout: PROCESSING 상태에서 타임아웃된 Outbox를 PENDING으로 복구
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.cancel-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class CancelOutboxScheduler {

    private final ProcessPendingCancelOutboxUseCase processPendingUseCase;
    private final RecoverTimeoutCancelOutboxUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.CancelOutbox config;

    public CancelOutboxScheduler(
            ProcessPendingCancelOutboxUseCase processPendingUseCase,
            RecoverTimeoutCancelOutboxUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().cancelOutbox();
    }

    /**
     * PENDING 상태의 취소 Outbox를 SQS로 발행합니다.
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 조회하고 SQS로 발행합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.cancel-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.cancel-outbox.process-pending.timezone}")
    @SchedulerJob("CancelOutbox-ProcessPending")
    public SchedulerBatchProcessingResult processPending() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingCancelOutboxCommand command =
                new ProcessPendingCancelOutboxCommand(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingUseCase.execute(command);
    }

    /**
     * PROCESSING 상태에서 타임아웃된 취소 Outbox를 PENDING으로 복구합니다.
     *
     * <p>PROCESSING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.cancel-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.cancel-outbox.recover-timeout.timezone}")
    @SchedulerJob("CancelOutbox-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeout() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        if (!recoverTimeout.enabled()) {
            return SchedulerBatchProcessingResult.of(0, 0, 0);
        }
        RecoverTimeoutCancelOutboxCommand command =
                new RecoverTimeoutCancelOutboxCommand(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutUseCase.execute(command);
    }
}
