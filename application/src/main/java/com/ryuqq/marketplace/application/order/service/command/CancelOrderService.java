package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemCancelCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.CancelOrderUseCase;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentCancelHelper;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 주문상품 취소 Service.
 *
 * <p>대상 주문상품을 조회하여 CANCELLED 상태로 전환합니다.
 */
@Service
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderCommandFactory factory;
    private final OrderItemReadManager readManager;
    private final OrderItemCommandManager commandManager;
    private final ShipmentCancelHelper shipmentCancelHelper;
    private final ShipmentCommandManager shipmentCommandManager;

    public CancelOrderService(
            OrderCommandFactory factory,
            OrderItemReadManager readManager,
            OrderItemCommandManager commandManager,
            ShipmentCancelHelper shipmentCancelHelper,
            ShipmentCommandManager shipmentCommandManager) {
        this.factory = factory;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.shipmentCancelHelper = shipmentCancelHelper;
        this.shipmentCommandManager = shipmentCommandManager;
    }

    @Override
    public void execute(OrderItemCancelCommand command) {
        StatusChangeContext<List<OrderItemId>> ctx = factory.createCancelContext(command);
        List<OrderItem> orderItems = readManager.findAllByIds(ctx.id());
        orderItems.forEach(
                item -> item.cancel(command.changedBy(), command.reason(), ctx.changedAt()));
        commandManager.persistAll(orderItems);

        List<OrderItemId> orderItemIds = orderItems.stream().map(OrderItem::id).toList();
        List<Shipment> cancelled =
                shipmentCancelHelper.cancelPreparingShipments(orderItemIds, ctx.changedAt());
        if (!cancelled.isEmpty()) {
            shipmentCommandManager.persistAll(cancelled);
        }
    }
}
