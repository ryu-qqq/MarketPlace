package com.ryuqq.marketplace.application.imagevariant.port.out.command;

import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;

/** ImageVariant Command Port. */
public interface ImageVariantCommandPort {

    Long persist(ImageVariant imageVariant);
}
