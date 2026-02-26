package com.ryuqq.marketplace.application.shipment.port.out.command;

import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.util.List;

/** Shipment Command Port. */
public interface ShipmentCommandPort {

    void persist(Shipment shipment);

    void persistAll(List<Shipment> shipments);
}
