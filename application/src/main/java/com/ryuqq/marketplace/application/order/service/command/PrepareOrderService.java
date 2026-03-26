package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.port.in.command.PrepareOrderUseCase;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 주문상품 발주확인(준비) Service.
 *
 * <p>대상 주문상품의 Shipment를 조회하여 PREPARING 상태로 전환합니다.
 */
@Service
public class PrepareOrderService implements PrepareOrderUseCase {

    private final OrderCommandFactory factory;
    private final ShipmentReadManager shipmentReadManager;
    private final ShipmentCommandManager shipmentCommandManager;

    public PrepareOrderService(
            OrderCommandFactory factory,
            ShipmentReadManager shipmentReadManager,
            ShipmentCommandManager shipmentCommandManager) {
        this.factory = factory;
        this.shipmentReadManager = shipmentReadManager;
        this.shipmentCommandManager = shipmentCommandManager;
    }

    @Override
    public void execute(OrderItemStatusCommand command) {
        StatusChangeContext<List<OrderItemId>> ctx = factory.createStatusChangeContext(command);
        List<Shipment> shipments = shipmentReadManager.findByOrderItemIds(ctx.id());
        shipments.forEach(s -> s.prepare(ctx.changedAt()));
        shipmentCommandManager.persistAll(shipments);
    }
}
