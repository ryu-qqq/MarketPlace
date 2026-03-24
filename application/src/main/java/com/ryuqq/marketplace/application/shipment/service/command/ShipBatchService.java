package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentBatchProcessor;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipBatchUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 송장등록 일괄 처리 Service.
 *
 * <p>orderItemNumber → orderItemId 변환 후 Factory에 위임합니다.
 */
@Service
public class ShipBatchService implements ShipBatchUseCase {

    private final ShipmentCommandFactory commandFactory;
    private final ShipmentBatchProcessor batchProcessor;
    private final OrderItemReadManager orderItemReadManager;

    public ShipBatchService(
            ShipmentCommandFactory commandFactory,
            ShipmentBatchProcessor batchProcessor,
            OrderItemReadManager orderItemReadManager) {
        this.commandFactory = commandFactory;
        this.batchProcessor = batchProcessor;
        this.orderItemReadManager = orderItemReadManager;
    }

    @Override
    public BatchProcessingResult<String> execute(ShipBatchCommand command) {
        // orderItemNumber → orderItemId 조회 후 Command 재구성
        List<ShipBatchItem> resolvedItems =
                command.items().stream().map(this::resolveOrderItemId).toList();
        ShipBatchCommand resolvedCommand = new ShipBatchCommand(resolvedItems);

        List<UpdateContext<OrderItemId, ShipmentShipData>> contexts =
                commandFactory.createShipContexts(resolvedCommand);

        Map<String, ShipBatchItem> itemMap =
                resolvedItems.stream()
                        .collect(Collectors.toMap(ShipBatchItem::orderItemId, item -> item));

        return batchProcessor.shipBatch(contexts, itemMap);
    }

    private ShipBatchItem resolveOrderItemId(ShipBatchItem item) {
        OrderItem orderItem =
                orderItemReadManager.getByOrderItemNumber(item.orderItemNumber());
        return new ShipBatchItem(
                orderItem.id().value(),
                item.orderItemNumber(),
                item.trackingNumber(),
                item.courierCode(),
                item.shipmentMethodType());
    }
}
