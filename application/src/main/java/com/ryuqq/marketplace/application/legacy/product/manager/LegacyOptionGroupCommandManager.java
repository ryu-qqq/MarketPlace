package com.ryuqq.marketplace.application.legacy.product.manager;

import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyOptionGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 옵션그룹 Command Manager. */
@Component
public class LegacyOptionGroupCommandManager {

    private final LegacyOptionGroupCommandPort commandPort;

    public LegacyOptionGroupCommandManager(LegacyOptionGroupCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(SellerOptionGroup optionGroup) {
        return commandPort.persist(optionGroup);
    }
}
