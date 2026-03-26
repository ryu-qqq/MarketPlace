package com.ryuqq.marketplace.application.imagevariantsync.port.out.command;

import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;

/**
 * ImageVariantSyncOutbox Command Port.
 *
 * <p>이미지 Variant Sync Outbox 저장을 위한 포트입니다.
 */
public interface ImageVariantSyncOutboxCommandPort {

    /**
     * Outbox를 저장합니다.
     *
     * @param outbox 저장할 Outbox
     * @return 저장된 Outbox ID
     */
    Long persist(ImageVariantSyncOutbox outbox);
}
