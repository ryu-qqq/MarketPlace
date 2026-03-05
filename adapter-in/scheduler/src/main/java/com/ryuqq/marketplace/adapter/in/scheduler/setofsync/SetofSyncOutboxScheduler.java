package com.ryuqq.marketplace.adapter.in.scheduler.setofsync;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.setofsync.dto.command.ProcessPendingSetofSyncCommand;
import com.ryuqq.marketplace.application.setofsync.dto.command.RecoverTimeoutSetofSyncCommand;
import com.ryuqq.marketplace.application.setofsync.port.in.command.ProcessPendingSetofSyncUseCase;
import com.ryuqq.marketplace.application.setofsync.port.in.command.RecoverTimeoutSetofSyncUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Setof Commerce 셀러 동기화 Outbox 처리 스케줄러.
 *
 * <p>두 가지 작업을 수행합니다:
 *
 * <ul>
 *   <li>processPendingOutboxes: PENDING 상태의 Outbox 처리
 *   <li>recoverTimeoutOutboxes: PROCESSING 타임아웃 Outbox 복구
 * </ul>
 *
 * <p>스케줄 주기 및 배치 크기는 환경별 설정 파일(scheduler-{profile}.yml)에서 관리됩니다.
 *
 * @see SchedulerProperties
 */
@Component
@ConditionalOnBean(ProcessPendingSetofSyncUseCase.class)
@ConditionalOnProperty(
        prefix = "scheduler.jobs.setof-sync-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SetofSyncOutboxScheduler {

    private static final Logger log = LoggerFactory.getLogger(SetofSyncOutboxScheduler.class);

    private final ProcessPendingSetofSyncUseCase processPendingUseCase;
    private final RecoverTimeoutSetofSyncUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.SetofSyncOutbox config;

    public SetofSyncOutboxScheduler(
            ProcessPendingSetofSyncUseCase processPendingUseCase,
            RecoverTimeoutSetofSyncUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().setofSyncOutbox();
    }

    /**
     * PENDING 상태의 Outbox를 처리합니다.
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 처리합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.setof-sync-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.setof-sync-outbox.process-pending.timezone}")
    @SchedulerJob("SetofSyncOutbox-ProcessPending")
    public void processPendingOutboxes() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        log.info(
                "Setof sync outbox 처리 스케줄러 시작. batchSize={}, delaySeconds={}",
                processPending.batchSize(),
                processPending.delaySeconds());
        ProcessPendingSetofSyncCommand command =
                ProcessPendingSetofSyncCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        processPendingUseCase.execute(command);
    }

    /**
     * PROCESSING 상태에서 타임아웃된 좀비 Outbox를 복구합니다.
     *
     * <p>PROCESSING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다. 실제 재처리는 다음
     * processPendingOutboxes 주기에서 수행됩니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.setof-sync-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.setof-sync-outbox.recover-timeout.timezone}")
    @SchedulerJob("SetofSyncOutbox-RecoverTimeout")
    public void recoverTimeoutOutboxes() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        log.info(
                "Setof sync outbox 타임아웃 복구 스케줄러 시작. batchSize={}, timeoutSeconds={}",
                recoverTimeout.batchSize(),
                recoverTimeout.timeoutSeconds());
        RecoverTimeoutSetofSyncCommand command =
                RecoverTimeoutSetofSyncCommand.of(
                        recoverTimeout.batchSize(), (int) recoverTimeout.timeoutSeconds());
        recoverTimeoutUseCase.execute(command);
    }
}
