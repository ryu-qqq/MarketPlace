package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory.ShipSingleContext;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentOutboxPayloadBuilder;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentPersistFacade;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipSingleUseCase;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import org.springframework.stereotype.Service;

/**
 * 단건 송장등록 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>APP-FAC-001: 수정은 Factory Context 사용.
 */
@Service
public class ShipSingleService implements ShipSingleUseCase {

    private final ShipmentReadManager readManager;
    private final ShipmentPersistFacade persistFacade;
    private final ShipmentCommandFactory commandFactory;

    public ShipSingleService(
            ShipmentReadManager readManager,
            ShipmentPersistFacade persistFacade,
            ShipmentCommandFactory commandFactory) {
        this.readManager = readManager;
        this.persistFacade = persistFacade;
        this.commandFactory = commandFactory;
    }

    @Override
    public void execute(ShipSingleCommand command) {
        ShipSingleContext context = commandFactory.createShipSingleContext(command);

        Shipment shipment = readManager.getByOrderItemId(context.orderItemId());
        ShipmentShipData shipData = context.shipData();
        shipment.ship(shipData.trackingNumber(), shipData.method(), context.changedAt());

        ShipmentOutbox outbox =
                ShipmentOutbox.forNew(
                        context.orderItemId(),
                        ShipmentOutboxType.SHIP,
                        ShipmentOutboxPayloadBuilder.shipPayload(command),
                        context.changedAt());

        persistFacade.persistWithOutbox(shipment, outbox);
    }
}
