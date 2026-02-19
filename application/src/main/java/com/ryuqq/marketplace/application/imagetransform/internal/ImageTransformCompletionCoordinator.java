package com.ryuqq.marketplace.application.imagetransform.internal;

import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagevariant.manager.ImageVariantCommandManager;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageDimension;
import com.ryuqq.marketplace.domain.imagevariant.vo.ResultAssetId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이미지 변환 완료 Coordinator.
 *
 * <p>ImageVariant 엔티티 생성과 Outbox 상태 완료를 원자적으로 처리합니다.
 */
@Component
public class ImageTransformCompletionCoordinator {

    private final ImageVariantCommandManager variantCommandManager;
    private final ImageTransformOutboxCommandManager outboxCommandManager;

    public ImageTransformCompletionCoordinator(
            ImageVariantCommandManager variantCommandManager,
            ImageTransformOutboxCommandManager outboxCommandManager) {
        this.variantCommandManager = variantCommandManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    /**
     * 이미지 변환 완료 처리를 원자적으로 수행합니다.
     *
     * @param outbox 완료 처리할 Outbox
     * @param response 변환 완료 응답
     * @param now 완료 시각
     */
    @Transactional
    public void complete(
            ImageTransformOutbox outbox, ImageTransformResponse response, Instant now) {
        ImageVariant variant =
                ImageVariant.forNew(
                        outbox.sourceImageId(),
                        outbox.sourceType(),
                        outbox.variantType(),
                        ResultAssetId.of(response.resultAssetId()),
                        ImageUrl.of(response.resultCdnUrl()),
                        ImageDimension.of(response.width(), response.height()),
                        now);

        variantCommandManager.persist(variant);

        outbox.complete(now);
        outboxCommandManager.persist(outbox);
    }
}
