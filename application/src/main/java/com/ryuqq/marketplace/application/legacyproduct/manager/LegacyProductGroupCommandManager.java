package com.ryuqq.marketplace.application.legacyproduct.manager;

import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyProductGroupCommandPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상품그룹 Command Manager. */
@Component
public class LegacyProductGroupCommandManager {

    private final LegacyProductGroupCommandPort commandPort;

    public LegacyProductGroupCommandManager(LegacyProductGroupCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(LegacyProductGroup productGroup) {
        return commandPort.persist(productGroup);
    }
}
