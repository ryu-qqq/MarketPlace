package com.ryuqq.marketplace.application.imagevariant.manager;

import com.ryuqq.marketplace.application.imagevariant.port.out.command.ImageVariantCommandPort;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ImageVariant Command Manager. */
@Component
public class ImageVariantCommandManager {

    private final ImageVariantCommandPort commandPort;

    public ImageVariantCommandManager(ImageVariantCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ImageVariant imageVariant) {
        return commandPort.persist(imageVariant);
    }
}
