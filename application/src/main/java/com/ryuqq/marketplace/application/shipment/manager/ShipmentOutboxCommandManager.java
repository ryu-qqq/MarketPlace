package com.ryuqq.marketplace.application.shipment.manager;

import com.ryuqq.marketplace.application.shipment.port.out.command.ShipmentOutboxCommandPort;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 배송 아웃박스 Write Manager. */
@Component
public class ShipmentOutboxCommandManager {

    private final ShipmentOutboxCommandPort commandPort;

    public ShipmentOutboxCommandManager(ShipmentOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ShipmentOutbox outbox) {
        return commandPort.persist(outbox);
    }

    @Transactional
    public void persistAll(List<ShipmentOutbox> outboxes) {
        commandPort.persistAll(outboxes);
    }
}
