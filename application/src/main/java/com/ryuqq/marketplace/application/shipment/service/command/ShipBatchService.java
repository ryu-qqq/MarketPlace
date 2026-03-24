package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentBatchProcessor;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipBatchUseCase;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    private final ShipmentCommandFactory commandFactory;
    private final ShipmentBatchProcessor batchProcessor;

    public ShipBatchService(
            ShipmentCommandFactory commandFactory, ShipmentBatchProcessor batchProcessor) {
        this.commandFactory = commandFactory;
        this.batchProcessor = batchProcessor;
    }

    @Override
    public BatchProcessingResult<String> execute(ShipBatchCommand command) {
        List<UpdateContext<OrderItemId, ShipmentShipData>> contexts =
                commandFactory.createShipContexts(command);
        Map<String, ShipBatchItem> itemMap =
                command.items().stream()
                        .collect(Collectors.toMap(ShipBatchItem::orderNumber, Function.identity()));
        return batchProcessor.shipBatch(contexts, itemMap);
    }
}
