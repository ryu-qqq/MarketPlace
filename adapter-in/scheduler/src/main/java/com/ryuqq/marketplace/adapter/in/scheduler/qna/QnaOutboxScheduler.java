package com.ryuqq.marketplace.adapter.in.scheduler.qna;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.qna.dto.command.ProcessPendingQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.dto.command.RecoverTimeoutQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.port.in.command.ProcessPendingQnaOutboxUseCase;
import com.ryuqq.marketplace.application.qna.port.in.command.RecoverTimeoutQnaOutboxUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * QnA 아웃박스 처리 스케줄러.
 *
 * <p>두 가지 작업을 수행합니다:
 * <ul>
 *   <li>processPending: PENDING Outbox를 조회하여 SQS로 발행
 *   <li>recoverTimeout: PROCESSING 상태에서 타임아웃된 Outbox를 PENDING으로 복구
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.qna-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class QnaOutboxScheduler {

    private final ProcessPendingQnaOutboxUseCase processPendingUseCase;
    private final RecoverTimeoutQnaOutboxUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.QnaOutbox config;

    public QnaOutboxScheduler(
            ProcessPendingQnaOutboxUseCase processPendingUseCase,
            RecoverTimeoutQnaOutboxUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().qnaOutbox();
    }

    @Scheduled(
            cron = "${scheduler.jobs.qna-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.qna-outbox.process-pending.timezone}")
    @SchedulerJob("QnaOutbox-ProcessPending")
    public void processPending() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingQnaOutboxCommand command =
                new ProcessPendingQnaOutboxCommand(processPending.batchSize(), processPending.delaySeconds());
        processPendingUseCase.execute(command);
    }

    @Scheduled(
            cron = "${scheduler.jobs.qna-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.qna-outbox.recover-timeout.timezone}")
    @SchedulerJob("QnaOutbox-RecoverTimeout")
    public void recoverTimeout() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        if (!recoverTimeout.enabled()) {
            return;
        }
        RecoverTimeoutQnaOutboxCommand command =
                new RecoverTimeoutQnaOutboxCommand(
                        recoverTimeout.batchSize(), (int) recoverTimeout.timeoutSeconds());
        recoverTimeoutUseCase.execute(command);
    }
}
