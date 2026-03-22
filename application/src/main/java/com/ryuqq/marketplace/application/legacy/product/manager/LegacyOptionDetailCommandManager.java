package com.ryuqq.marketplace.application.legacy.product.manager;

import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyOptionDetailCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 옵션상세 Command Manager. */
@Component
public class LegacyOptionDetailCommandManager {

    private final LegacyOptionDetailCommandPort commandPort;

    public LegacyOptionDetailCommandManager(LegacyOptionDetailCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(SellerOptionValue optionValue) {
        return commandPort.persist(optionValue);
    }
}
