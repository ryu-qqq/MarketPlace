package com.ryuqq.marketplace.application.legacy.description.manager;

import com.ryuqq.marketplace.application.legacy.description.port.out.command.LegacyProductDescriptionCommandPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroupDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDescription;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상세설명 Command Manager. */
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

    /** content/cdnPath/publishStatus를 포함한 상세설명 저장. */
    @Transactional
    public void persistDescription(LegacyProductGroupDescription description) {
        commandPort.persistDescription(description);
    }
}
