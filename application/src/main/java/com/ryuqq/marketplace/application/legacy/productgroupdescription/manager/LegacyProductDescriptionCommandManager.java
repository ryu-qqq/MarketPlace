package com.ryuqq.marketplace.application.legacy.productgroupdescription.manager;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command.LegacyProductDescriptionCommandPort;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyProductGroupDescription;
import com.ryuqq.marketplace.domain.legacy.productdescription.vo.LegacyProductDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
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
    public void persist(LegacyProductGroupId productGroupId, LegacyProductDescription description) {
        commandPort.persist(productGroupId, description);
    }

    @Transactional
    public void persistDescription(LegacyProductGroupDescription description) {
        commandPort.persistDescription(description);
    }

    @Transactional
    public void update(UpdateProductGroupDescriptionCommand command) {
        commandPort.update(command);
    }
}
