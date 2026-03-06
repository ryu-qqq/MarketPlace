package com.ryuqq.marketplace.adapter.in.scheduler.outboundseller;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.outboundseller.dto.command.ProcessPendingOutboundSellerCommand;
import com.ryuqq.marketplace.application.outboundseller.dto.command.RecoverTimeoutOutboundSellerCommand;
import com.ryuqq.marketplace.application.outboundseller.port.in.command.ProcessPendingOutboundSellerUseCase;
import com.ryuqq.marketplace.application.outboundseller.port.in.command.RecoverTimeoutOutboundSellerUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 외부 셀러 동기화 Outbox 처리 스케줄러.
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
@ConditionalOnBean(ProcessPendingOutboundSellerUseCase.class)
@ConditionalOnProperty(
        prefix = "scheduler.jobs.outbound-seller-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class OutboundSellerOutboxScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboundSellerOutboxScheduler.class);

    private final ProcessPendingOutboundSellerUseCase processPendingUseCase;
    private final RecoverTimeoutOutboundSellerUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.OutboundSellerOutbox config;

    public OutboundSellerOutboxScheduler(
            ProcessPendingOutboundSellerUseCase processPendingUseCase,
            RecoverTimeoutOutboundSellerUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().outboundSellerOutbox();
    }

    @Scheduled(
            cron = "${scheduler.jobs.outbound-seller-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.outbound-seller-outbox.process-pending.timezone}")
    @SchedulerJob("OutboundSellerOutbox-ProcessPending")
    public void processPendingOutboxes() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        log.info(
                "Outbound seller outbox 처리 스케줄러 시작. batchSize={}, delaySeconds={}",
                processPending.batchSize(),
                processPending.delaySeconds());
        ProcessPendingOutboundSellerCommand command =
                ProcessPendingOutboundSellerCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        processPendingUseCase.execute(command);
    }

    @Scheduled(
            cron = "${scheduler.jobs.outbound-seller-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.outbound-seller-outbox.recover-timeout.timezone}")
    @SchedulerJob("OutboundSellerOutbox-RecoverTimeout")
    public void recoverTimeoutOutboxes() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        log.info(
                "Outbound seller outbox 타임아웃 복구 스케줄러 시작. batchSize={}, timeoutSeconds={}",
                recoverTimeout.batchSize(),
                recoverTimeout.timeoutSeconds());
        RecoverTimeoutOutboundSellerCommand command =
                RecoverTimeoutOutboundSellerCommand.of(
                        recoverTimeout.batchSize(), (int) recoverTimeout.timeoutSeconds());
        recoverTimeoutUseCase.execute(command);
    }
}
