package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentWriteManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipBatchUseCase;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/** 송장등록 일괄 처리 Service. */
@Service
public class ShipBatchService implements ShipBatchUseCase {

    private final ShipmentReadManager readManager;
    private final ShipmentWriteManager writeManager;
    private final ShipmentCommandFactory commandFactory;
    private final TimeProvider timeProvider;

    public ShipBatchService(
            ShipmentReadManager readManager,
            ShipmentWriteManager writeManager,
            ShipmentCommandFactory commandFactory,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.writeManager = writeManager;
        this.commandFactory = commandFactory;
        this.timeProvider = timeProvider;
    }

    @Override
    public BatchProcessingResult<String> execute(ShipBatchCommand command) {
        Instant now = timeProvider.now();
        List<BatchItemResult<String>> results = new ArrayList<>();

        for (ShipBatchItem item : command.items()) {
            try {
                Shipment shipment = readManager.getById(ShipmentId.of(item.shipmentId()));
                ShipmentMethod method =
                        commandFactory.createShipmentMethod(
                                item.shipmentMethodType(), item.courierCode(), item.courierName());
                shipment.ship(item.trackingNumber(), method, now);
                writeManager.persist(shipment);
                results.add(BatchItemResult.success(item.shipmentId()));
            } catch (Exception e) {
                results.add(
                        BatchItemResult.failure(
                                item.shipmentId(), e.getClass().getSimpleName(), e.getMessage()));
            }
        }

        return BatchProcessingResult.from(results);
    }
}
