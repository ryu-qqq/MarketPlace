package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductImageCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductImageReadManager;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImages;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImages.ImageDiffResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 이미지 Coordinator.
 *
 * <p>이미지 저장 및 diff 기반 업데이트를 담당합니다. 도메인 객체 생성은 Factory에서 수행하며, Coordinator는 조회·비교·영속만 처리합니다.
 */
@Component
public class LegacyImageCoordinator {

    private final TimeProvider timeProvider;
    private final LegacyProductImageReadManager imageReadManager;
    private final LegacyProductImageCommandManager imageCommandManager;

    public LegacyImageCoordinator(
            TimeProvider timeProvider,
            LegacyProductImageReadManager imageReadManager,
            LegacyProductImageCommandManager imageCommandManager) {
        this.timeProvider = timeProvider;
        this.imageReadManager = imageReadManager;
        this.imageCommandManager = imageCommandManager;
    }

    /** 이미지 일괄 저장 (등록용). */
    public void persistImages(List<LegacyProductImage> images) {
        imageCommandManager.persistAll(images);
    }

    /**
     * 이미지 수정 (diff 기반).
     *
     * <p>기존 삭제되지 않은 이미지를 조회하고 displayOrder + originUrl 기준으로 변경/추가/삭제를 판단하여 persistAll로 일괄 처리합니다.
     */
    public void updateImages(LegacyProductGroupId groupId, List<LegacyProductImage> newImages) {
        LegacyProductImages existingImages = imageReadManager.getByProductGroupId(groupId);

        ImageDiffResult diffResult = existingImages.diff(newImages, timeProvider.now());

        if (diffResult.hasChanges()) {
            imageCommandManager.persistAll(diffResult.toPersist());
        }
    }
}
