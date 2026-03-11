package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ProcessPendingShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentOutboxProcessor;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ProcessPendingShipmentOutboxUseCase;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 대기 중인 배송 아웃박스 처리 서비스.
 *
 * <p>PENDING 상태의 배송 아웃박스를 조회하여 외부 채널에 상태를 동기화합니다.
 */
@Service
public class ProcessPendingShipmentOutboxService implements ProcessPendingShipmentOutboxUseCase {

    private final ShipmentOutboxReadManager outboxReadManager;
    private final ShipmentOutboxProcessor outboxProcessor;

    public ProcessPendingShipmentOutboxService(
            ShipmentOutboxReadManager outboxReadManager, ShipmentOutboxProcessor outboxProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.outboxProcessor = outboxProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingShipmentOutboxCommand command) {
        List<ShipmentOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ShipmentOutbox outbox : outboxes) {
            boolean success = outboxProcessor.processOutbox(outbox);
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
