package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import org.springframework.stereotype.Component;

/** 반품 반려 전략. */
@Component
public class LegacyRejectRefundStrategy implements LegacyOrderUpdateStrategy {

    private final LegacyOrderCommandFacade orderCommandFacade;

    public LegacyRejectRefundStrategy(LegacyOrderCommandFacade orderCommandFacade) {
        this.orderCommandFacade = orderCommandFacade;
    }

    @Override
    public String supportedStatus() {
        return "RETURN_REQUEST_REJECTED";
    }

    @Override
    public void execute(LegacyOrderUpdateCommand command) {
        orderCommandFacade.updateStatusAndHistory(command);
    }
}
