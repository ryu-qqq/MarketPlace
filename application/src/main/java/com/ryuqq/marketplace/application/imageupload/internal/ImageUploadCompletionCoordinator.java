package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이미지 업로드 완료 Coordinator.
 *
 * <p>Provider에서 sourceType에 맞는 전략을 꺼내 업로드 완료 처리를 위임하고, Outbox 상태를 COMPLETED로 변경합니다.
 */
@Component
public class ImageUploadCompletionCoordinator {

    private final ImageUploadCompletionStrategyProvider strategyProvider;
    private final ImageUploadOutboxCommandManager outboxCommandManager;

    public ImageUploadCompletionCoordinator(
            ImageUploadCompletionStrategyProvider strategyProvider,
            ImageUploadOutboxCommandManager outboxCommandManager) {
        this.strategyProvider = strategyProvider;
        this.outboxCommandManager = outboxCommandManager;
    }

    /**
     * 이미지 업로드 완료 처리를 원자적으로 수행합니다.
     *
     * @param outbox 처리할 Outbox
     * @param newCdnUrl 업로드된 CDN URL
     * @param fileAssetId FileFlow 에셋 ID (null 가능)
     * @param now 완료 시각
     */
    @Transactional
    public void complete(
            ImageUploadOutbox outbox, String newCdnUrl, String fileAssetId, Instant now) {
        ImageUrl uploadedUrl = ImageUrl.of(newCdnUrl);

        strategyProvider
                .getStrategy(outbox.sourceType())
                .complete(outbox.sourceId(), uploadedUrl, fileAssetId);

        outbox.complete(now);
        outboxCommandManager.persist(outbox);
    }
}
