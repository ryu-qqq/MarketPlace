package com.ryuqq.marketplace.adapter.in.scheduler.outboundsync;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.outboundsync.dto.command.ProcessPendingOutboundSyncCommand;
import com.ryuqq.marketplace.application.outboundsync.dto.command.RecoverTimeoutOutboundSyncCommand;
import com.ryuqq.marketplace.application.outboundsync.port.in.command.ProcessPendingOutboundSyncUseCase;
import com.ryuqq.marketplace.application.outboundsync.port.in.command.RecoverTimeoutOutboundSyncUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * OutboundSync Outbox Relay 스케줄러.
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
        prefix = "scheduler.jobs.outbound-sync-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class OutboundSyncOutboxScheduler {

    private final ProcessPendingOutboundSyncUseCase processPendingUseCase;
    private final RecoverTimeoutOutboundSyncUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.OutboundSyncOutbox config;

    public OutboundSyncOutboxScheduler(
            ProcessPendingOutboundSyncUseCase processPendingUseCase,
            RecoverTimeoutOutboundSyncUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().outboundSyncOutbox();
    }

    /**
     * PENDING 상태의 OutboundSync Outbox를 SQS로 발행합니다.
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 조회하고 SQS로 발행합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.outbound-sync-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.outbound-sync-outbox.process-pending.timezone}")
    @SchedulerJob("OutboundSyncOutbox-ProcessPending")
    public SchedulerBatchProcessingResult processPending() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingOutboundSyncCommand command =
                ProcessPendingOutboundSyncCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingUseCase.execute(command);
    }

    /**
     * PROCESSING 상태에서 타임아웃된 Outbox를 PENDING으로 복구합니다.
     *
     * <p>PROCESSING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.outbound-sync-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.outbound-sync-outbox.recover-timeout.timezone}")
    @SchedulerJob("OutboundSyncOutbox-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeout() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        if (!recoverTimeout.enabled()) {
            return SchedulerBatchProcessingResult.of(0, 0, 0);
        }
        RecoverTimeoutOutboundSyncCommand command =
                RecoverTimeoutOutboundSyncCommand.of(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutUseCase.execute(command);
    }
}
