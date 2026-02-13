package com.ryuqq.marketplace.application.productgroupdescription.manager;

import com.ryuqq.marketplace.application.productgroupdescription.port.out.command.DescriptionImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** DescriptionImage Command Manager. */
@Component
public class DescriptionImageCommandManager {

    private final DescriptionImageCommandPort commandPort;

    public DescriptionImageCommandManager(DescriptionImageCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void deleteByDescriptionId(Long descriptionId) {
        commandPort.deleteByDescriptionId(descriptionId);
    }

    @Transactional
    public List<Long> persistAll(Long descriptionId, List<DescriptionImage> images) {
        return commandPort.persistAll(descriptionId, images);
    }
}
