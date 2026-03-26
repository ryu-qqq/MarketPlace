package com.ryuqq.marketplace.application.legacy.productgroupdescription.manager;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command.LegacyProductDescriptionCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상세설명 Command Manager. */
@Component
public class LegacyProductDescriptionCommandManager {

    private final LegacyProductDescriptionCommandPort commandPort;

    public LegacyProductDescriptionCommandManager(LegacyProductDescriptionCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ProductGroupDescription description) {
        return commandPort.persist(description);
    }
}
