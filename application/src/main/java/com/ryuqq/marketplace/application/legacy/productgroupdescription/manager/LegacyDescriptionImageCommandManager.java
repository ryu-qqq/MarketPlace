package com.ryuqq.marketplace.application.legacy.productgroupdescription.manager;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command.LegacyDescriptionImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상세설명 이미지 Command Manager. */
@Component
public class LegacyDescriptionImageCommandManager {

    private final LegacyDescriptionImageCommandPort commandPort;

    public LegacyDescriptionImageCommandManager(LegacyDescriptionImageCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(DescriptionImage image) {
        return commandPort.persist(image);
    }
}
