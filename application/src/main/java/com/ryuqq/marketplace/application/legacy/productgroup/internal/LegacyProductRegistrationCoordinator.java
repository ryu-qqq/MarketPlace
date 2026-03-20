package com.ryuqq.marketplace.application.legacy.productgroup.internal;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.internal.LegacyDescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroupimage.internal.LegacyImageCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productnotice.internal.LegacyNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.product.internal.LegacyDeliveryCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.product.internal.LegacySkuCoordinator;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupSaveResult;
import com.ryuqq.marketplace.application.legacy.shared.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
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
 * <p>등록 흐름을 오케스트레이션합니다. 세부 로직은 각 전문 코디네이터에게 위임합니다.
 */
@Component
public class LegacyProductRegistrationCoordinator {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyNoticeCommandCoordinator noticeCommandCoordinator;
    private final LegacyDeliveryCommandCoordinator deliveryCommandCoordinator;
    private final LegacyDescriptionCommandCoordinator descriptionCommandCoordinator;
    private final LegacyImageCommandCoordinator imageCommandCoordinator;
    private final LegacySkuCoordinator skuCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductRegistrationCoordinator(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductGroupCommandCoordinator productGroupCommandCoordinator,
            LegacyNoticeCommandCoordinator noticeCommandCoordinator,
            LegacyDeliveryCommandCoordinator deliveryCommandCoordinator,
            LegacyDescriptionCommandCoordinator descriptionCommandCoordinator,
            LegacyImageCommandCoordinator imageCommandCoordinator,
            LegacySkuCoordinator skuCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.deliveryCommandCoordinator = deliveryCommandCoordinator;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.skuCoordinator = skuCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Transactional
    public LegacyProductGroupSaveResult register(LegacyProductRegistrationBundle bundle) {
        Long productGroupId = productGroupCommandCoordinator.register(bundle.productGroup());
        LegacyProductGroupId groupId = LegacyProductGroupId.of(productGroupId);

        noticeCommandCoordinator.register(groupId, bundle.productGroup().notice());
        deliveryCommandCoordinator.register(groupId, bundle.productGroup().delivery());
        descriptionCommandCoordinator.register(
                groupId, bundle.productGroup().description().detailDescription());

        List<LegacyProductImage> images =
                commandFactory.createImagesForRegistration(groupId, bundle.images());
        imageCommandCoordinator.register(images);

        List<Long> productIds = skuCoordinator.registerSkus(groupId, bundle.skus());

        LegacyConversionOutbox outbox =
                LegacyConversionOutbox.forNew(productGroupId, Instant.now());
        conversionOutboxCommandManager.persist(outbox);

        return new LegacyProductGroupSaveResult(productGroupId, productIds);
    }
}
