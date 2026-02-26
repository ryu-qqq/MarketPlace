package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ConfirmShipmentBatchUseCase;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 발주확인 일괄 처리 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>APP-FAC-001: 상태변경은 BulkStatusChangeContext 사용.
 */
@Service
public class ConfirmShipmentBatchService implements ConfirmShipmentBatchUseCase {

    private final ShipmentReadManager readManager;
    private final ShipmentCommandManager writeManager;
    private final ShipmentCommandFactory commandFactory;

    public ConfirmShipmentBatchService(
            ShipmentReadManager readManager,
            ShipmentCommandManager writeManager,
            ShipmentCommandFactory commandFactory) {
        this.readManager = readManager;
        this.writeManager = writeManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public BatchProcessingResult<String> execute(ConfirmShipmentBatchCommand command) {
        BulkStatusChangeContext<ShipmentId> context = commandFactory.createConfirmContexts(command);
        Instant changedAt = context.changedAt();

        List<BatchItemResult<String>> results = new ArrayList<>();

        for (ShipmentId shipmentId : context.ids()) {
            try {
                Shipment shipment = readManager.getById(shipmentId);
                shipment.prepare(changedAt);
                writeManager.persist(shipment);
                results.add(BatchItemResult.success(shipmentId.value()));
            } catch (Exception e) {
                results.add(
                        BatchItemResult.failure(
                                shipmentId.value(), e.getClass().getSimpleName(), e.getMessage()));
            }
        }

        return BatchProcessingResult.from(results);
    }
}
