package com.ryuqq.marketplace.application.imagetransform.manager;

import com.ryuqq.marketplace.application.imagetransform.port.out.command.ImageTransformOutboxCommandPort;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ImageTransformOutbox Command Manager. */
@Component
public class ImageTransformOutboxCommandManager {

    private final ImageTransformOutboxCommandPort commandPort;

    public ImageTransformOutboxCommandManager(ImageTransformOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ImageTransformOutbox outbox) {
        return commandPort.persist(outbox);
    }

    @Transactional
    public void persistAll(List<ImageTransformOutbox> outboxes) {
        for (ImageTransformOutbox outbox : outboxes) {
            commandPort.persist(outbox);
        }
    }
}
