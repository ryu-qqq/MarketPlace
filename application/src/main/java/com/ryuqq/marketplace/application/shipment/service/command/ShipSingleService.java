package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentWriteManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipSingleUseCase;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 단건 송장등록 Service. */
@Service
public class ShipSingleService implements ShipSingleUseCase {

    private final ShipmentReadManager readManager;
    private final ShipmentWriteManager writeManager;
    private final ShipmentCommandFactory commandFactory;
    private final TimeProvider timeProvider;

    public ShipSingleService(
            ShipmentReadManager readManager,
            ShipmentWriteManager writeManager,
            ShipmentCommandFactory commandFactory,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.writeManager = writeManager;
        this.commandFactory = commandFactory;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(ShipSingleCommand command) {
        Instant now = timeProvider.now();

        Shipment shipment = readManager.getByOrderId(command.orderId());
        ShipmentMethod method =
                commandFactory.createShipmentMethod(
                        command.shipmentMethodType(), command.courierCode(), command.courierName());
        shipment.ship(command.trackingNumber(), method, now);
        writeManager.persist(shipment);
    }
}
