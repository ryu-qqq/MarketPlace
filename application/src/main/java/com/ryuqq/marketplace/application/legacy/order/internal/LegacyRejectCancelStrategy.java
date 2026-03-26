package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.legacy.order.manager.LegacyShipmentCommandManager;
import org.springframework.stereotype.Component;

/** 취소 반려 전략. claimRejectedAndShipmentOrder인 경우 재배송도 처리. */
@Component
public class LegacyRejectCancelStrategy implements LegacyOrderUpdateStrategy {

    private final LegacyOrderCommandFacade orderCommandFacade;
    private final LegacyShipmentCommandManager shipmentCommandManager;

    public LegacyRejectCancelStrategy(
            LegacyOrderCommandFacade orderCommandFacade,
            LegacyShipmentCommandManager shipmentCommandManager) {
        this.orderCommandFacade = orderCommandFacade;
        this.shipmentCommandManager = shipmentCommandManager;
    }

    @Override
    public String supportedStatus() {
        return "CANCEL_REQUEST_REJECTED";
    }

    @Override
    public void execute(LegacyOrderUpdateCommand command) {
        orderCommandFacade.updateStatusAndHistory(command);

        if ("claimRejectedAndShipmentOrder".equals(command.type())) {
            shipmentCommandManager.updateShipmentInfo(
                    command.orderId(),
                    command.invoiceNo() != null ? command.invoiceNo() : "",
                    command.courierCode() != null ? command.courierCode() : "",
                    command.shipmentType() != null ? command.shipmentType() : "");
        }
    }
}
