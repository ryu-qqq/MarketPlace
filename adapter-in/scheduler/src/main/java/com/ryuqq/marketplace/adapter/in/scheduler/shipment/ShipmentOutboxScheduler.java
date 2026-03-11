package com.ryuqq.marketplace.adapter.in.scheduler.shipment;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ProcessPendingShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.RecoverTimeoutShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.port.in.command.ProcessPendingShipmentOutboxUseCase;
import com.ryuqq.marketplace.application.shipment.port.in.command.RecoverTimeoutShipmentOutboxUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 배송 아웃박스 처리 스케줄러.
 *
 * <p>두 가지 작업을 수행합니다:
 *
 * <ul>
 *   <li>processPendingOutboxes: PENDING 상태의 배송 아웃박스 처리
 *   <li>recoverTimeoutOutboxes: PROCESSING 타임아웃 아웃박스 복구
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.shipment-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class ShipmentOutboxScheduler {

    private final ProcessPendingShipmentOutboxUseCase processPendingUseCase;
    private final RecoverTimeoutShipmentOutboxUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.ShipmentOutbox config;

    public ShipmentOutboxScheduler(
            ProcessPendingShipmentOutboxUseCase processPendingUseCase,
            RecoverTimeoutShipmentOutboxUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().shipmentOutbox();
    }

    @Scheduled(
            cron = "${scheduler.jobs.shipment-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.shipment-outbox.process-pending.timezone}")
    @SchedulerJob("ShipmentOutbox-ProcessPending")
    public SchedulerBatchProcessingResult processPendingOutboxes() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingShipmentOutboxCommand command =
                ProcessPendingShipmentOutboxCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingUseCase.execute(command);
    }

    @Scheduled(
            cron = "${scheduler.jobs.shipment-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.shipment-outbox.recover-timeout.timezone}")
    @SchedulerJob("ShipmentOutbox-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeoutOutboxes() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        RecoverTimeoutShipmentOutboxCommand command =
                RecoverTimeoutShipmentOutboxCommand.of(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutUseCase.execute(command);
    }
}
