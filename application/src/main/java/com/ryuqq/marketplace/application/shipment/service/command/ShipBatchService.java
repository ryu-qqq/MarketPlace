package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipBatchUseCase;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 송장등록 일괄 처리 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>APP-FAC-001: 수정은 UpdateContext 사용.
 */
@Service
public class ShipBatchService implements ShipBatchUseCase {

    private final ShipmentReadManager readManager;
    private final ShipmentCommandManager writeManager;
    private final ShipmentCommandFactory commandFactory;

    public ShipBatchService(
            ShipmentReadManager readManager,
            ShipmentCommandManager writeManager,
            ShipmentCommandFactory commandFactory) {
        this.readManager = readManager;
        this.writeManager = writeManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public BatchProcessingResult<String> execute(ShipBatchCommand command) {
        List<UpdateContext<ShipmentId, ShipmentShipData>> contexts =
                commandFactory.createShipContexts(command);

        List<BatchItemResult<String>> results = new ArrayList<>();

        for (UpdateContext<ShipmentId, ShipmentShipData> ctx : contexts) {
            try {
                Shipment shipment = readManager.getById(ctx.id());
                ShipmentShipData shipData = ctx.updateData();
                shipment.ship(shipData.trackingNumber(), shipData.method(), ctx.changedAt());
                writeManager.persist(shipment);
                results.add(BatchItemResult.success(ctx.id().value()));
            } catch (Exception e) {
                results.add(
                        BatchItemResult.failure(
                                ctx.id().value(), e.getClass().getSimpleName(), e.getMessage()));
            }
        }

        return BatchProcessingResult.from(results);
    }
}
