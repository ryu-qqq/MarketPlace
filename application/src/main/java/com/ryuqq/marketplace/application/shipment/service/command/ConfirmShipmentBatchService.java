package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentWriteManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ConfirmShipmentBatchUseCase;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/** 발주확인 일괄 처리 Service. */
@Service
public class ConfirmShipmentBatchService implements ConfirmShipmentBatchUseCase {

    private final ShipmentReadManager readManager;
    private final ShipmentWriteManager writeManager;
    private final TimeProvider timeProvider;

    public ConfirmShipmentBatchService(
            ShipmentReadManager readManager,
            ShipmentWriteManager writeManager,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.writeManager = writeManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public BatchProcessingResult<String> execute(ConfirmShipmentBatchCommand command) {
        Instant now = timeProvider.now();
        List<BatchItemResult<String>> results = new ArrayList<>();

        for (String shipmentId : command.shipmentIds()) {
            try {
                Shipment shipment = readManager.getById(ShipmentId.of(shipmentId));
                shipment.prepare(now);
                writeManager.persist(shipment);
                results.add(BatchItemResult.success(shipmentId));
            } catch (Exception e) {
                results.add(
                        BatchItemResult.failure(
                                shipmentId, e.getClass().getSimpleName(), e.getMessage()));
            }
        }

        return BatchProcessingResult.from(results);
    }
}
