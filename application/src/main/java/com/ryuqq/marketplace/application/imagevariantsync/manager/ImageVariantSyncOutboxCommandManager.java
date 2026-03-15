package com.ryuqq.marketplace.application.imagevariantsync.manager;

import com.ryuqq.marketplace.application.imagevariantsync.port.out.command.ImageVariantSyncOutboxCommandPort;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ImageVariantSyncOutbox Command Manager. */
@Component
public class ImageVariantSyncOutboxCommandManager {

    private final ImageVariantSyncOutboxCommandPort commandPort;

    public ImageVariantSyncOutboxCommandManager(ImageVariantSyncOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ImageVariantSyncOutbox outbox) {
        return commandPort.persist(outbox);
    }
}
