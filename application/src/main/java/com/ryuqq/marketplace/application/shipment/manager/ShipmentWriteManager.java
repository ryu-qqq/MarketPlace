package com.ryuqq.marketplace.application.shipment.manager;

import com.ryuqq.marketplace.application.shipment.port.out.command.ShipmentCommandPort;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Shipment Write Manager. */
@Component
public class ShipmentWriteManager {

    private final ShipmentCommandPort commandPort;

    public ShipmentWriteManager(ShipmentCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(Shipment shipment) {
        commandPort.persist(shipment);
    }

    @Transactional
    public void persistAll(List<Shipment> shipments) {
        commandPort.persistAll(shipments);
    }
}
