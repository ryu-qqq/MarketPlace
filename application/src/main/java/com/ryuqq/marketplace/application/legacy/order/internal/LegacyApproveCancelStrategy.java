package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import org.springframework.stereotype.Component;

/** 취소 승인 전략. */
@Component
public class LegacyApproveCancelStrategy implements LegacyOrderUpdateStrategy {

    private final LegacyOrderCommandFacade orderCommandFacade;

    public LegacyApproveCancelStrategy(LegacyOrderCommandFacade orderCommandFacade) {
        this.orderCommandFacade = orderCommandFacade;
    }

    @Override
    public String supportedStatus() {
        return "CANCEL_REQUEST_CONFIRMED";
    }

    @Override
    public void execute(LegacyOrderUpdateCommand command) {
        orderCommandFacade.updateStatusAndHistory(command);
    }
}
