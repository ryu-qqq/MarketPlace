package com.ryuqq.marketplace.application.shipment.internal;

import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBundle;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 배송 퍼시스트 파사드.
 *
 * <p>Shipment + ShipmentOutbox + OrderItem을 같은 트랜잭션에서 일괄 저장합니다.
 */
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

    /** 발주확인 번들 일괄 저장: Shipment 생성 + Outbox 생성 + OrderItem 상태 변경. */
    @Transactional
    public void persistConfirmBundle(ConfirmShipmentBundle bundle) {
        shipmentCommandManager.persistAll(bundle.shipments());
        outboxCommandManager.persistAll(bundle.outboxes());
        orderItemCommandManager.updateStatusAll(bundle.orderItems());
    }

    @Transactional
    public void persistAllWithOutboxes(List<Shipment> shipments, List<ShipmentOutbox> outboxes) {
        shipmentCommandManager.persistAll(shipments);
        outboxCommandManager.persistAll(outboxes);
    }

    @Transactional
    public void persistWithOutbox(Shipment shipment, ShipmentOutbox outbox) {
        shipmentCommandManager.persist(shipment);
        outboxCommandManager.persist(outbox);
    }
}
