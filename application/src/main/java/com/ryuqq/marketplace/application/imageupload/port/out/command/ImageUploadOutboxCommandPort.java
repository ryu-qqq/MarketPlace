package com.ryuqq.marketplace.application.imageupload.port.out.command;

import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;

/** ImageUploadOutbox Command Port. */
public interface ImageUploadOutboxCommandPort {

    Long persist(ImageUploadOutbox outbox);
}
