package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import org.springframework.stereotype.Component;

/** 배송 완료 전략. */
@Component
public class LegacyDeliveryCompleteStrategy implements LegacyOrderUpdateStrategy {

    private final LegacyOrderCommandFacade orderCommandFacade;

    public LegacyDeliveryCompleteStrategy(LegacyOrderCommandFacade orderCommandFacade) {
        this.orderCommandFacade = orderCommandFacade;
    }

    @Override
    public String supportedStatus() {
        return "DELIVERY_COMPLETED";
    }

    @Override
    public void execute(LegacyOrderUpdateCommand command) {
        orderCommandFacade.updateStatusAndHistory(command);
    }
}
