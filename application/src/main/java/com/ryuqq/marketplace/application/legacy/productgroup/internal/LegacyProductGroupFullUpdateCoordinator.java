package com.ryuqq.marketplace.application.legacy.productgroup.internal;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.product.internal.LegacySellerOptionCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.internal.LegacyDescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroupimage.internal.LegacyImageCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productnotice.manager.LegacyProductNoticeCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품그룹 전체 수정 Coordinator.
 *
 * <p>표준 ProductGroupUpdateBundle을 받아 soft delete + 재등록 패턴으로 처리합니다. Update 타입을 Register 타입으로 변환하여 하위
 * Coordinator에 위임합니다.
 */
@Component
public class LegacyProductGroupFullUpdateCoordinator {

    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyProductNoticeCommandManager noticeCommandManager;
    private final LegacyDescriptionCommandCoordinator descriptionCommandCoordinator;
    private final LegacyImageCommandCoordinator imageCommandCoordinator;
    private final LegacySellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    private final LegacyProductCommandCoordinator productCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductGroupFullUpdateCoordinator(
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
    public void update(ProductGroupUpdateBundle bundle) {
        long productGroupId = bundle.basicInfoUpdateData().productGroupId().value();

        // 1. ProductGroup 기본정보
        long regularPrice =
                bundle.productEntries().isEmpty()
                        ? 0L
                        : bundle.productEntries().getFirst().regularPrice();
        long currentPrice =
                bundle.productEntries().isEmpty()
                        ? 0L
                        : bundle.productEntries().getFirst().currentPrice();

        productGroupCommandCoordinator.update(
                bundle.basicInfoUpdateData(), regularPrice, currentPrice);

        // 2. Notice
        noticeCommandManager.register(
                new RegisterProductNoticeCommand(
                        productGroupId,
                        bundle.noticeCommand().noticeCategoryId(),
                        bundle.noticeCommand().entries().stream()
                                .map(
                                        e ->
                                                new RegisterProductNoticeCommand.NoticeEntryCommand(
                                                        e.noticeFieldId(), e.fieldValue()))
                                .toList()));

        // 3. Description
        descriptionCommandCoordinator.update(bundle.descriptionCommand());

        // 4. Image
        imageCommandCoordinator.update(bundle.imageCommand());

        // 5. OptionGroups → Update → Register 변환 후 등록
        RegisterSellerOptionGroupsCommand registerOptionCmd =
                toRegisterOptionCommand(productGroupId, bundle.optionGroupCommand());
        List<SellerOptionValueId> allOptionValueIds =
                sellerOptionCommandCoordinator.register(registerOptionCmd);

        // 6. Products → DiffEntry → ProductData 변환 후 soft delete + 재등록
        List<RegisterProductsCommand.ProductData> productDataList =
                toProductDataList(bundle.productEntries());
        productCommandCoordinator.update(
                productGroupId,
                productDataList,
                registerOptionCmd.optionGroups(),
                allOptionValueIds,
                Instant.now());

        // 7. ConversionOutbox
        conversionOutboxCommandManager.createIfNoPending(productGroupId, Instant.now());
    }

    private RegisterSellerOptionGroupsCommand toRegisterOptionCommand(
            long productGroupId, UpdateSellerOptionGroupsCommand updateCmd) {
        List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> groups =
                updateCmd.optionGroups().stream()
                        .map(
                                g ->
                                        new RegisterSellerOptionGroupsCommand.OptionGroupCommand(
                                                g.optionGroupName(),
                                                g.canonicalOptionGroupId(),
                                                g.inputType(),
                                                g.optionValues().stream()
                                                        .map(
                                                                v ->
                                                                        new RegisterSellerOptionGroupsCommand
                                                                                .OptionValueCommand(
                                                                                v.optionValueName(),
                                                                                v
                                                                                        .canonicalOptionValueId(),
                                                                                v.sortOrder()))
                                                        .toList()))
                        .toList();
        return new RegisterSellerOptionGroupsCommand(productGroupId, "COMBINATION", groups);
    }

    private List<RegisterProductsCommand.ProductData> toProductDataList(
            List<ProductDiffUpdateEntry> entries) {
        return entries.stream()
                .map(
                        e ->
                                new RegisterProductsCommand.ProductData(
                                        e.skuCode(),
                                        e.regularPrice(),
                                        e.currentPrice(),
                                        e.stockQuantity(),
                                        e.sortOrder(),
                                        e.selectedOptions()))
                .toList();
    }
}
