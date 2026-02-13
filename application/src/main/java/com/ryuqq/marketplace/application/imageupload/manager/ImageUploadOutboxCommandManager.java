package com.ryuqq.marketplace.application.imageupload.manager;

import com.ryuqq.marketplace.application.imageupload.port.out.command.ImageUploadOutboxCommandPort;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ImageUploadOutbox Command Manager. */
@Component
public class ImageUploadOutboxCommandManager {

    private final ImageUploadOutboxCommandPort commandPort;

    public ImageUploadOutboxCommandManager(ImageUploadOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ImageUploadOutbox outbox) {
        return commandPort.persist(outbox);
    }
}
