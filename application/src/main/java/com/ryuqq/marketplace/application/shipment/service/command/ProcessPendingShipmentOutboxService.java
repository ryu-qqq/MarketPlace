package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ProcessPendingShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentOutboxRelayProcessor;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ProcessPendingShipmentOutboxUseCase;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** PENDING 배송 아웃박스를 조회하여 SQS로 발행하는 서비스. */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "shipment-outbox")
public class ProcessPendingShipmentOutboxService implements ProcessPendingShipmentOutboxUseCase {

    private final ShipmentOutboxReadManager outboxReadManager;
    private final ShipmentOutboxRelayProcessor relayProcessor;

    public ProcessPendingShipmentOutboxService(
            ShipmentOutboxReadManager outboxReadManager,
            ShipmentOutboxRelayProcessor relayProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.relayProcessor = relayProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingShipmentOutboxCommand command) {
        List<ShipmentOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ShipmentOutbox outbox : outboxes) {
            boolean success = relayProcessor.relay(outbox);
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
