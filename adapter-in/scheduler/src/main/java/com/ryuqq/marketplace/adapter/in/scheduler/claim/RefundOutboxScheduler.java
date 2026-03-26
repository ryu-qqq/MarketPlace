package com.ryuqq.marketplace.adapter.in.scheduler.claim;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.ProcessPendingRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RecoverTimeoutRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.port.in.command.ProcessPendingRefundOutboxUseCase;
import com.ryuqq.marketplace.application.refund.port.in.command.RecoverTimeoutRefundOutboxUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 환불 아웃박스 처리 스케줄러.
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
        prefix = "scheduler.jobs.refund-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class RefundOutboxScheduler {

    private final ProcessPendingRefundOutboxUseCase processPendingUseCase;
    private final RecoverTimeoutRefundOutboxUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.RefundOutbox config;

    public RefundOutboxScheduler(
            ProcessPendingRefundOutboxUseCase processPendingUseCase,
            RecoverTimeoutRefundOutboxUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().refundOutbox();
    }

    /**
     * PENDING 상태의 환불 Outbox를 SQS로 발행합니다.
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 조회하고 SQS로 발행합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.refund-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.refund-outbox.process-pending.timezone}")
    @SchedulerJob("RefundOutbox-ProcessPending")
    public SchedulerBatchProcessingResult processPending() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingRefundOutboxCommand command =
                new ProcessPendingRefundOutboxCommand(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingUseCase.execute(command);
    }

    /**
     * PROCESSING 상태에서 타임아웃된 환불 Outbox를 PENDING으로 복구합니다.
     *
     * <p>PROCESSING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.refund-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.refund-outbox.recover-timeout.timezone}")
    @SchedulerJob("RefundOutbox-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeout() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        if (!recoverTimeout.enabled()) {
            return SchedulerBatchProcessingResult.of(0, 0, 0);
        }
        RecoverTimeoutRefundOutboxCommand command =
                new RecoverTimeoutRefundOutboxCommand(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutUseCase.execute(command);
    }
}
