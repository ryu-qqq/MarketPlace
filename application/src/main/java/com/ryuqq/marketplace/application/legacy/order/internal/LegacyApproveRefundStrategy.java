package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import org.springframework.stereotype.Component;

/** 반품 승인 전략. */
@Component
public class LegacyApproveRefundStrategy implements LegacyOrderUpdateStrategy {

    private final LegacyOrderCommandFacade orderCommandFacade;

    public LegacyApproveRefundStrategy(LegacyOrderCommandFacade orderCommandFacade) {
        this.orderCommandFacade = orderCommandFacade;
    }

    @Override
    public String supportedStatus() {
        return "RETURN_REQUEST_CONFIRMED";
    }

    @Override
    public void execute(LegacyOrderUpdateCommand command) {
        orderCommandFacade.updateStatusAndHistory(command);
    }
}
