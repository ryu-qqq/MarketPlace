package com.ryuqq.marketplace.adapter.in.scheduler.selleradmin;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ProcessPendingSellerAdminOutboxCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RecoverTimeoutSellerAdminOutboxCommand;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ProcessPendingSellerAdminOutboxUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.RecoverTimeoutSellerAdminOutboxUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 셀러 관리자 인증 Outbox 처리 스케줄러.
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
@ConditionalOnProperty(
        prefix = "scheduler.jobs.seller-admin-auth-outbox.process-pending",
        name = "enabled",
        havingValue = "true")
public class SellerAdminAuthOutboxScheduler {

    private final ProcessPendingSellerAdminOutboxUseCase processPendingOutboxUseCase;
    private final RecoverTimeoutSellerAdminOutboxUseCase recoverTimeoutOutboxUseCase;
    private final SchedulerProperties.SellerAdminAuthOutbox config;

    public SellerAdminAuthOutboxScheduler(
            ProcessPendingSellerAdminOutboxUseCase processPendingOutboxUseCase,
            RecoverTimeoutSellerAdminOutboxUseCase recoverTimeoutOutboxUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingOutboxUseCase = processPendingOutboxUseCase;
        this.recoverTimeoutOutboxUseCase = recoverTimeoutOutboxUseCase;
        this.config = schedulerProperties.jobs().sellerAdminAuthOutbox();
    }

    /**
     * PENDING 상태의 Outbox를 처리합니다.
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 처리합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.seller-admin-auth-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.seller-admin-auth-outbox.process-pending.timezone}")
    @SchedulerJob("SellerAdminAuthOutbox-ProcessPending")
    public SchedulerBatchProcessingResult processPendingOutboxes() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingSellerAdminOutboxCommand command =
                ProcessPendingSellerAdminOutboxCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingOutboxUseCase.execute(command);
    }

    /**
     * PROCESSING 상태에서 타임아웃된 좀비 Outbox를 복구합니다.
     *
     * <p>PROCESSING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다. 실제 재처리는 다음
     * processPendingOutboxes 주기에서 수행됩니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.seller-admin-auth-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.seller-admin-auth-outbox.recover-timeout.timezone}")
    @SchedulerJob("SellerAdminAuthOutbox-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeoutOutboxes() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        RecoverTimeoutSellerAdminOutboxCommand command =
                new RecoverTimeoutSellerAdminOutboxCommand(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutOutboxUseCase.execute(command);
    }
}
