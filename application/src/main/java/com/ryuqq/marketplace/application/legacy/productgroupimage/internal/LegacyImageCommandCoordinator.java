package com.ryuqq.marketplace.application.legacy.productgroupimage.internal;

import com.ryuqq.marketplace.application.legacy.productgroupimage.manager.LegacyProductImageCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroupimage.manager.LegacyProductImageReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.factory.ProductGroupImageFactory;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImageDiff;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImageUpdateData;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 이미지 Command Coordinator.
 *
 * <p>표준 도메인 객체 기반으로 레거시 DB(luxurydb)에 저장합니다.
 * 이미지 업로드 Outbox는 생성하지 않습니다 — 레거시 컨버전 과정에서 처리됩니다.
 */
@Component
public class LegacyImageCommandCoordinator {

    private final ProductGroupImageFactory imageFactory;
    private final LegacyProductImageReadManager imageReadManager;
    private final LegacyProductImageCommandManager imageCommandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyImageCommandCoordinator(
            ProductGroupImageFactory imageFactory,
            LegacyProductImageReadManager imageReadManager,
            LegacyProductImageCommandManager imageCommandManager,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.imageFactory = imageFactory;
        this.imageReadManager = imageReadManager;
        this.imageCommandManager = imageCommandManager;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    /** 이미지 등록 (상품그룹 등록 시 사용). Command → 표준 도메인 생성 → persist. */
    @Transactional
    public void register(RegisterProductGroupImagesCommand command) {
        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());
        ProductGroupImages images =
                imageFactory.createFromImageRegistration(productGroupId, command.images());
        for (ProductGroupImage image : images.toList()) {
            imageCommandManager.persist(image);
        }
    }

    /** 이미지 수정 (단독 수정 시 사용). 기존 로드 → diff → persist + ConversionOutbox. */
    @Transactional
    public void update(UpdateProductGroupImagesCommand command) {
        ProductGroupImageUpdateData updateData = imageFactory.createUpdateData(command);
        ProductGroupImages existing = imageReadManager.getByProductGroupId(command.productGroupId());
        ProductGroupImageDiff diff = existing.update(updateData);

        for (ProductGroupImage image : diff.removed()) {
            imageCommandManager.persist(image);
        }
        for (ProductGroupImage image : diff.added()) {
            imageCommandManager.persist(image);
        }

        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
