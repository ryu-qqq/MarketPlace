package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.RecoverTimeoutShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.RecoverTimeoutShipmentOutboxUseCase;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 타임아웃된 배송 아웃박스 복구 서비스. */
@Service
public class RecoverTimeoutShipmentOutboxService implements RecoverTimeoutShipmentOutboxUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutShipmentOutboxService.class);

    private final ShipmentOutboxReadManager outboxReadManager;
    private final ShipmentOutboxCommandManager outboxCommandManager;
    private final ShipmentCommandFactory commandFactory;

    public RecoverTimeoutShipmentOutboxService(
            ShipmentOutboxReadManager outboxReadManager,
            ShipmentOutboxCommandManager outboxCommandManager,
            ShipmentCommandFactory commandFactory) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public SchedulerBatchProcessingResult execute(RecoverTimeoutShipmentOutboxCommand command) {
        Instant timeoutThreshold = commandFactory.resolveTimeoutThreshold(command);
        List<ShipmentOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        timeoutThreshold, command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ShipmentOutbox outbox : outboxes) {
            try {
                Instant now =
                        commandFactory.createOutboxTransitionContext(outbox.idValue()).changedAt();
                outbox.recoverFromTimeout(now);
                outboxCommandManager.persist(outbox);
                successCount++;
            } catch (Exception e) {
                log.error(
                        "배송 Outbox 복구 실패: outboxId={}, orderItemId={}, error={}",
                        outbox.idValue(),
                        outbox.orderItemIdValue(),
                        e.getMessage(),
                        e);
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
