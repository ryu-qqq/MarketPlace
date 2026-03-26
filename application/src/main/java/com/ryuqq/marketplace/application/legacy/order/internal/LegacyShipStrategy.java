package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.legacy.order.manager.LegacyShipmentCommandManager;
import org.springframework.stereotype.Component;

/** 배송 시작 (송장 입력) 전략. */
@Component
public class LegacyShipStrategy implements LegacyOrderUpdateStrategy {

    private final LegacyOrderCommandFacade orderCommandFacade;
    private final LegacyShipmentCommandManager shipmentCommandManager;

    public LegacyShipStrategy(
            LegacyOrderCommandFacade orderCommandFacade,
            LegacyShipmentCommandManager shipmentCommandManager) {
        this.orderCommandFacade = orderCommandFacade;
        this.shipmentCommandManager = shipmentCommandManager;
    }

    @Override
    public String supportedStatus() {
        return "DELIVERY_PROCESSING";
    }

    @Override
    public void execute(LegacyOrderUpdateCommand command) {
        orderCommandFacade.updateStatusAndHistory(command);
        shipmentCommandManager.updateShipmentInfo(
                command.orderId(),
                command.invoiceNo() != null ? command.invoiceNo() : "",
                command.courierCode() != null ? command.courierCode() : "",
                command.shipmentType() != null ? command.shipmentType() : "");
    }
}
