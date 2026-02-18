package com.ryuqq.marketplace.application.imagetransform.port.out.command;

import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;

/** ImageTransformOutbox Command Port. */
public interface ImageTransformOutboxCommandPort {

    Long persist(ImageTransformOutbox outbox);
}
