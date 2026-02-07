package com.ryuqq.marketplace.adapter.in.scheduler.selleradmin;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ProcessPendingEmailOutboxCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RecoverTimeoutEmailOutboxCommand;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ProcessPendingEmailOutboxUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.RecoverTimeoutEmailOutboxUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 셀러 관리자 이메일 Outbox 처리 스케줄러.
 *
 * <p>두 가지 작업을 수행합니다:
 *
 * <ul>
 *   <li>processPendingOutboxes: PENDING 상태의 이메일 Outbox 처리
 *   <li>recoverTimeoutOutboxes: PROCESSING 타임아웃 이메일 Outbox 복구
 * </ul>
 *
 * <p>스케줄 주기 및 배치 크기는 환경별 설정 파일(scheduler-{profile}.yml)에서 관리됩니다.
 *
 * @see SchedulerProperties
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.seller-admin-email-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SellerAdminEmailOutboxScheduler {

    private final ProcessPendingEmailOutboxUseCase processPendingOutboxUseCase;
    private final RecoverTimeoutEmailOutboxUseCase recoverTimeoutOutboxUseCase;
    private final SchedulerProperties.SellerAdminEmailOutbox config;

    public SellerAdminEmailOutboxScheduler(
            ProcessPendingEmailOutboxUseCase processPendingOutboxUseCase,
            RecoverTimeoutEmailOutboxUseCase recoverTimeoutOutboxUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingOutboxUseCase = processPendingOutboxUseCase;
        this.recoverTimeoutOutboxUseCase = recoverTimeoutOutboxUseCase;
        this.config = schedulerProperties.jobs().sellerAdminEmailOutbox();
    }

    /**
     * PENDING 상태의 이메일 Outbox를 처리합니다.
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 처리합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.seller-admin-email-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.seller-admin-email-outbox.process-pending.timezone}")
    @SchedulerJob("SellerAdminEmailOutbox-ProcessPending")
    public SchedulerBatchProcessingResult processPendingOutboxes() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingEmailOutboxCommand command =
                ProcessPendingEmailOutboxCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingOutboxUseCase.execute(command);
    }

    /**
     * PROCESSING 상태에서 타임아웃된 좀비 이메일 Outbox를 복구합니다.
     *
     * <p>PROCESSING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다. 실제 재처리는 다음
     * processPendingOutboxes 주기에서 수행됩니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.seller-admin-email-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.seller-admin-email-outbox.recover-timeout.timezone}")
    @SchedulerJob("SellerAdminEmailOutbox-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeoutOutboxes() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        RecoverTimeoutEmailOutboxCommand command =
                RecoverTimeoutEmailOutboxCommand.of(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutOutboxUseCase.execute(command);
    }
}
