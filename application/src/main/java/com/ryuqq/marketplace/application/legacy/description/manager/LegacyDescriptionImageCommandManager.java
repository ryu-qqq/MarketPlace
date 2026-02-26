package com.ryuqq.marketplace.application.legacy.description.manager;

import com.ryuqq.marketplace.application.legacy.description.port.out.command.LegacyDescriptionImageCommandPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyDescriptionImage;
import java.util.List;
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
    public void persistAll(List<LegacyDescriptionImage> images) {
        commandPort.persistAll(images);
    }

    @Transactional
    public void softDeleteAll(List<LegacyDescriptionImage> images) {
        commandPort.softDeleteAll(images);
    }
}
