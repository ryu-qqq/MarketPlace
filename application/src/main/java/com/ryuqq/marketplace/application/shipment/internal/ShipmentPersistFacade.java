package com.ryuqq.marketplace.application.shipment.internal;

import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ShipmentPersistFacade {

    private final ShipmentCommandManager shipmentCommandManager;
    private final ShipmentOutboxCommandManager outboxCommandManager;
    private final OrderItemCommandManager orderItemCommandManager;

    public ShipmentPersistFacade(
            ShipmentCommandManager shipmentCommandManager,
            ShipmentOutboxCommandManager outboxCommandManager,
            OrderItemCommandManager orderItemCommandManager) {
        this.shipmentCommandManager = shipmentCommandManager;
        this.outboxCommandManager = outboxCommandManager;
        this.orderItemCommandManager = orderItemCommandManager;
    }

    @Transactional
    public void persistAll(ShipmentPersistenceBundle bundle) {
        if (!bundle.shipments().isEmpty()) {
            shipmentCommandManager.persistAll(bundle.shipments());
        }
        if (!bundle.outboxes().isEmpty()) {
            outboxCommandManager.persistAll(bundle.outboxes());
        }
        if (!bundle.orderItems().isEmpty()) {
            orderItemCommandManager.persistAll(bundle.orderItems());
        }
    }
}
