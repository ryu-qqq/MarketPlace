package com.ryuqq.marketplace.application.legacy.order.manager;

import com.ryuqq.marketplace.application.legacy.order.port.out.command.LegacyShipmentCommandPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 배송(shipment) 커맨드 매니저. */
@Component
public class LegacyShipmentCommandManager {

    private final LegacyShipmentCommandPort commandPort;

    public LegacyShipmentCommandManager(LegacyShipmentCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void updateShipmentInfo(
            long orderId, String invoiceNo, String courierCode, String shipmentType) {
        commandPort.updateShipmentInfo(orderId, invoiceNo, courierCode, shipmentType);
    }
}
