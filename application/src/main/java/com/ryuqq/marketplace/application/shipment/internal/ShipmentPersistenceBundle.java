package com.ryuqq.marketplace.application.shipment.internal;

import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.util.List;

public record ShipmentPersistenceBundle(
        List<Shipment> shipments, List<ShipmentOutbox> outboxes, List<OrderItem> orderItems) {

    public static ShipmentPersistenceBundle of(
            List<Shipment> shipments, List<ShipmentOutbox> outboxes, List<OrderItem> orderItems) {
        return new ShipmentPersistenceBundle(shipments, outboxes, orderItems);
    }

    public static ShipmentPersistenceBundle ofShipmentsAndOutboxes(
            List<Shipment> shipments, List<ShipmentOutbox> outboxes) {
        return new ShipmentPersistenceBundle(shipments, outboxes, List.of());
    }

    public static ShipmentPersistenceBundle ofSingleWithOutbox(
            Shipment shipment, ShipmentOutbox outbox) {
        return new ShipmentPersistenceBundle(List.of(shipment), List.of(outbox), List.of());
    }
}
