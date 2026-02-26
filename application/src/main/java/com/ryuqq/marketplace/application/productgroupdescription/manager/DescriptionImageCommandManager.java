package com.ryuqq.marketplace.application.productgroupdescription.manager;

import com.ryuqq.marketplace.application.productgroupdescription.port.out.command.DescriptionImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import java.util.ArrayList;
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
    public Long persist(DescriptionImage image) {
        return commandPort.persist(image);
    }

    @Transactional
    public List<Long> persistAll(List<DescriptionImage> images) {
        List<Long> ids = new ArrayList<>();
        for (DescriptionImage image : images) {
            ids.add(commandPort.persist(image));
        }
        return ids;
    }
}
