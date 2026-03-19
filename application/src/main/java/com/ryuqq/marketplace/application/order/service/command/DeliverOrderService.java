package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.port.in.command.DeliverOrderUseCase;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 주문상품 배송완료 Service.
 *
 * <p>대상 주문상품의 Shipment를 조회하여 DELIVERED 상태로 전환합니다.
 */
@Service
public class DeliverOrderService implements DeliverOrderUseCase {

    private final ShipmentReadManager shipmentReadManager;
    private final ShipmentCommandManager shipmentCommandManager;
    private final TimeProvider timeProvider;

    public DeliverOrderService(
            ShipmentReadManager shipmentReadManager,
            ShipmentCommandManager shipmentCommandManager,
            TimeProvider timeProvider) {
        this.shipmentReadManager = shipmentReadManager;
        this.shipmentCommandManager = shipmentCommandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(OrderItemStatusCommand command) {
        Instant now = timeProvider.now();
        List<OrderItemId> orderItemIds = command.orderItemIds().stream()
                .map(OrderItemId::of)
                .toList();
        List<Shipment> shipments = shipmentReadManager.findByOrderItemIds(orderItemIds);
        shipments.forEach(s -> s.deliver(now));
        shipmentCommandManager.persistAll(shipments);
    }
}
