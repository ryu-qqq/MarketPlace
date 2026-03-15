package com.ryuqq.marketplace.application.imagetransform.internal;

import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformCompletionBundle;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagevariant.manager.ImageVariantCommandManager;
import com.ryuqq.marketplace.application.imagevariantsync.manager.ImageVariantSyncOutboxCommandManager;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이미지 변환 완료 Coordinator.
 *
 * <p>Factory에서 생성한 도메인 객체 번들을 원자적으로 저장합니다.
 */
@Component
public class ImageTransformCompletionCoordinator {

    private final ImageVariantCommandManager variantCommandManager;
    private final ImageTransformOutboxCommandManager outboxCommandManager;
    private final ImageVariantSyncOutboxCommandManager syncOutboxCommandManager;

    public ImageTransformCompletionCoordinator(
            ImageVariantCommandManager variantCommandManager,
            ImageTransformOutboxCommandManager outboxCommandManager,
            ImageVariantSyncOutboxCommandManager syncOutboxCommandManager) {
        this.variantCommandManager = variantCommandManager;
        this.outboxCommandManager = outboxCommandManager;
        this.syncOutboxCommandManager = syncOutboxCommandManager;
    }

    /**
     * 이미지 변환 완료 처리를 원자적으로 수행합니다.
     *
     * @param outbox 완료 처리할 Outbox
     * @param bundle Factory에서 생성한 도메인 객체 번들
     * @param now 완료 시각
     */
    @Transactional
    public void complete(
            ImageTransformOutbox outbox, ImageTransformCompletionBundle bundle, Instant now) {
        variantCommandManager.persist(bundle.variant());

        outbox.complete(now);
        outboxCommandManager.persist(outbox);

        bundle.syncOutbox().ifPresent(syncOutboxCommandManager::persist);
    }
}
