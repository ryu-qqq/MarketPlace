package com.ryuqq.marketplace.application.legacy.productgroup.internal;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.product.internal.LegacySellerOptionCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.internal.LegacyDescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroupimage.internal.LegacyImageCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productnotice.manager.LegacyProductNoticeCommandManager;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupSaveResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품그룹 등록 Coordinator.
 *
 * <p>표준 FullProductGroupRegistrationCoordinator와 동일한 패턴.
 * 동일한 Bundle, 동일한 흐름. Port만 레거시입니다.
 */
@Component
public class LegacyProductRegistrationCoordinator {

    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyProductNoticeCommandManager noticeCommandManager;
    private final LegacyDescriptionCommandCoordinator descriptionCommandCoordinator;
    private final LegacyImageCommandCoordinator imageCommandCoordinator;
    private final LegacySellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    private final LegacyProductCommandCoordinator productCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductRegistrationCoordinator(
            LegacyProductGroupCommandCoordinator productGroupCommandCoordinator,
            LegacyProductNoticeCommandManager noticeCommandManager,
            LegacyDescriptionCommandCoordinator descriptionCommandCoordinator,
            LegacyImageCommandCoordinator imageCommandCoordinator,
            LegacySellerOptionCommandCoordinator sellerOptionCommandCoordinator,
            LegacyProductCommandCoordinator productCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.noticeCommandManager = noticeCommandManager;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.sellerOptionCommandCoordinator = sellerOptionCommandCoordinator;
        this.productCommandCoordinator = productCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Transactional
    public LegacyProductGroupSaveResult register(ProductGroupRegistrationBundle bundle) {

        // 1. ProductGroup
        long regularPrice = bundle.products().isEmpty() ? 0L
                : bundle.products().getFirst().regularPrice();
        long currentPrice = bundle.products().isEmpty() ? 0L
                : bundle.products().getFirst().currentPrice();

        Long productGroupId = productGroupCommandCoordinator.register(
                bundle.productGroup(), regularPrice, currentPrice);

        // 2. Notice
        noticeCommandManager.register(
                new RegisterProductNoticeCommand(
                        productGroupId, bundle.noticeCategoryId(), bundle.noticeEntries()));

        // 3. Description
        descriptionCommandCoordinator.register(
                new RegisterProductGroupDescriptionCommand(
                        productGroupId, bundle.descriptionContent()));

        // 4. Image
        imageCommandCoordinator.register(
                new RegisterProductGroupImagesCommand(productGroupId, bundle.images()));

        // 5. OptionGroups → 옵션 등록 + SellerOptionValueId 획득
        List<SellerOptionValueId> allOptionValueIds =
                sellerOptionCommandCoordinator.register(
                        new RegisterSellerOptionGroupsCommand(
                                productGroupId, bundle.optionType(), bundle.optionGroups()));

        // 6. Products → 이름 기반 옵션 resolve 후 등록
        List<Long> productIds = productCommandCoordinator.registerWithOptionResolve(
                productGroupId,
                bundle.products(),
                bundle.optionGroups(),
                allOptionValueIds,
                bundle.createdAt());

        // 7. ConversionOutbox
        LegacyConversionOutbox outbox =
                LegacyConversionOutbox.forNew(productGroupId, bundle.createdAt());
        conversionOutboxCommandManager.persist(outbox);

        return new LegacyProductGroupSaveResult(productGroupId, productIds);
    }
}
