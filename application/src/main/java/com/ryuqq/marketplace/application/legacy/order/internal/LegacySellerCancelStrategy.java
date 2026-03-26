package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import org.springframework.stereotype.Component;

/** 셀러 직접 취소 (품절 등) 전략. */
@Component
public class LegacySellerCancelStrategy implements LegacyOrderUpdateStrategy {

    private final LegacyOrderCommandFacade orderCommandFacade;

    public LegacySellerCancelStrategy(LegacyOrderCommandFacade orderCommandFacade) {
        this.orderCommandFacade = orderCommandFacade;
    }

    @Override
    public String supportedStatus() {
        return "SALE_CANCELLED";
    }

    @Override
    public void execute(LegacyOrderUpdateCommand command) {
        orderCommandFacade.updateStatusAndHistory(command);
    }
}
