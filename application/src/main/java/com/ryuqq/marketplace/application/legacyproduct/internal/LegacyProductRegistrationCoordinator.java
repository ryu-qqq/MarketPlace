package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.dto.bundle.LegacyProductRegistrationBundle;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupSaveResult;
import com.ryuqq.marketplace.application.legacyproduct.facade.LegacyProductGroupRegistrationFacade;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품그룹 등록 Coordinator.
 *
 * <p>등록 흐름을 오케스트레이션합니다. 세부 로직은 각 전문 코디네이터(ImageCoordinator, SkuCoordinator)에게 위임합니다.
 */
@Component
public class LegacyProductRegistrationCoordinator {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductGroupRegistrationFacade productGroupFacade;
    private final LegacyImageCoordinator imageCoordinator;
    private final LegacySkuCoordinator skuCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductRegistrationCoordinator(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductGroupRegistrationFacade productGroupFacade,
            LegacyImageCoordinator imageCoordinator,
            LegacySkuCoordinator skuCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.productGroupFacade = productGroupFacade;
        this.imageCoordinator = imageCoordinator;
        this.skuCoordinator = skuCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Transactional
    public LegacyProductGroupSaveResult register(LegacyProductRegistrationBundle bundle) {
        Long productGroupId = productGroupFacade.register(bundle.productGroup());
        LegacyProductGroupId groupId = LegacyProductGroupId.of(productGroupId);

        List<LegacyProductImage> images =
                commandFactory.createImagesForRegistration(groupId, bundle.images());
        imageCoordinator.persistImages(images);
        List<Long> productIds = skuCoordinator.registerSkus(groupId, bundle.skus());

        LegacyConversionOutbox outbox =
                LegacyConversionOutbox.forNew(productGroupId, Instant.now());
        conversionOutboxCommandManager.persist(outbox);

        return new LegacyProductGroupSaveResult(productGroupId, productIds);
    }
}
