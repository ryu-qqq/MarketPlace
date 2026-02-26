package com.ryuqq.marketplace.application.productgroup.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductGroup 번들 생성 전용 Factory.
 *
 * <p>ProductGroupRegistrationBundle, ProductGroupUpdateBundle 생성을 담당합니다. 각 per-package
 * Register/Update Command의 inner record를 직접 사용하여 중간 변환 레이어 없이 번들을 구성합니다.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@SuppressWarnings("PMD.ExcessiveImports")
@Component
public class ProductGroupBundleFactory {

    private final TimeProvider timeProvider;

    public ProductGroupBundleFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 상품 그룹 등록 번들 생성.
     *
     * @param command 등록 Command
     * @return 등록 번들
     */
    public ProductGroupRegistrationBundle createProductGroupBundle(
            RegisterProductGroupCommand command) {
        Instant now = timeProvider.now();

        ProductGroup productGroup =
                ProductGroup.forNew(
                        SellerId.of(command.sellerId()),
                        BrandId.of(command.brandId()),
                        CategoryId.of(command.categoryId()),
                        ShippingPolicyId.of(command.shippingPolicyId()),
                        RefundPolicyId.of(command.refundPolicyId()),
                        ProductGroupName.of(command.productGroupName()),
                        OptionType.valueOf(command.optionType()),
                        now);

        return new ProductGroupRegistrationBundle(
                productGroup,
                toImageCommands(command.images()),
                command.optionType(),
                toOptionGroupCommands(command.optionGroups()),
                command.description().content(),
                command.notice().noticeCategoryId(),
                toNoticeEntryCommands(command.notice().entries()),
                toProductDataList(command.products()),
                now);
    }

    /**
     * 상품 그룹 수정 번들 생성.
     *
     * @param command 수정 Command
     * @return 수정 번들
     */
    public ProductGroupUpdateBundle createUpdateBundle(UpdateProductGroupFullCommand command) {
        long productGroupId = command.productGroupId();
        Instant now = timeProvider.now();

        ProductGroupUpdateData basicInfoUpdateData =
                ProductGroupUpdateData.of(
                        ProductGroupId.of(productGroupId),
                        ProductGroupName.of(command.productGroupName()),
                        BrandId.of(command.brandId()),
                        CategoryId.of(command.categoryId()),
                        ShippingPolicyId.of(command.shippingPolicyId()),
                        RefundPolicyId.of(command.refundPolicyId()),
                        OptionType.valueOf(command.optionType()),
                        now);

        UpdateProductGroupImagesCommand imageCommand =
                new UpdateProductGroupImagesCommand(
                        productGroupId, toUpdateImageCommands(command.images()));

        UpdateSellerOptionGroupsCommand optionGroupCommand =
                new UpdateSellerOptionGroupsCommand(
                        productGroupId,
                        command.optionGroups() != null
                                ? toUpdateOptionGroupCommands(command.optionGroups())
                                : List.of());

        UpdateProductGroupDescriptionCommand descriptionCommand =
                new UpdateProductGroupDescriptionCommand(
                        productGroupId, command.description().content());

        UpdateProductNoticeCommand noticeCommand =
                new UpdateProductNoticeCommand(
                        productGroupId,
                        command.notice().noticeCategoryId(),
                        toUpdateNoticeEntryCommands(command.notice().entries()));

        List<ProductDiffUpdateEntry> productEntries = toProductDiffEntries(command.products());

        return new ProductGroupUpdateBundle(
                basicInfoUpdateData,
                imageCommand,
                optionGroupCommand,
                descriptionCommand,
                noticeCommand,
                productEntries);
    }

    // === Registration: per-package Command inner record 변환 ===

    private static List<RegisterProductGroupImagesCommand.ImageCommand> toImageCommands(
            List<RegisterProductGroupCommand.ImageCommand> images) {
        return images.stream()
                .map(
                        img ->
                                new RegisterProductGroupImagesCommand.ImageCommand(
                                        img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private static List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> toOptionGroupCommands(
            List<RegisterProductGroupCommand.OptionGroupCommand> optionGroups) {
        if (optionGroups == null) {
            return List.of();
        }
        return optionGroups.stream()
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
                                                                        v.canonicalOptionValueId(),
                                                                        v.sortOrder()))
                                                .toList()))
                .toList();
    }

    private static List<RegisterProductNoticeCommand.NoticeEntryCommand> toNoticeEntryCommands(
            List<RegisterProductGroupCommand.NoticeEntryCommand> entries) {
        return entries.stream()
                .map(
                        e ->
                                new RegisterProductNoticeCommand.NoticeEntryCommand(
                                        e.noticeFieldId(), e.fieldValue()))
                .toList();
    }

    private static List<RegisterProductsCommand.ProductData> toProductDataList(
            List<RegisterProductGroupCommand.ProductCommand> products) {
        return products.stream()
                .map(
                        p ->
                                new RegisterProductsCommand.ProductData(
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        p.selectedOptions()))
                .toList();
    }

    // === Update: per-package Command 변환 ===

    private static List<UpdateProductGroupImagesCommand.ImageCommand> toUpdateImageCommands(
            List<UpdateProductGroupFullCommand.ImageCommand> images) {
        return images.stream()
                .map(
                        img ->
                                new UpdateProductGroupImagesCommand.ImageCommand(
                                        img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private static List<UpdateSellerOptionGroupsCommand.OptionGroupCommand>
            toUpdateOptionGroupCommands(
                    List<UpdateProductGroupFullCommand.OptionGroupCommand> optionGroups) {
        return optionGroups.stream()
                .map(
                        g ->
                                new UpdateSellerOptionGroupsCommand.OptionGroupCommand(
                                        g.sellerOptionGroupId(),
                                        g.optionGroupName(),
                                        g.canonicalOptionGroupId(),
                                        g.inputType(),
                                        g.optionValues().stream()
                                                .map(
                                                        v ->
                                                                new UpdateSellerOptionGroupsCommand
                                                                        .OptionValueCommand(
                                                                        v.sellerOptionValueId(),
                                                                        v.optionValueName(),
                                                                        v.canonicalOptionValueId(),
                                                                        v.sortOrder()))
                                                .toList()))
                .toList();
    }

    private static List<UpdateProductNoticeCommand.NoticeEntryCommand> toUpdateNoticeEntryCommands(
            List<UpdateProductGroupFullCommand.NoticeEntryCommand> entries) {
        return entries.stream()
                .map(
                        e ->
                                new UpdateProductNoticeCommand.NoticeEntryCommand(
                                        e.noticeFieldId(), e.fieldValue()))
                .toList();
    }

    private static List<ProductDiffUpdateEntry> toProductDiffEntries(
            List<UpdateProductGroupFullCommand.ProductCommand> products) {
        return products.stream()
                .map(
                        p ->
                                new ProductDiffUpdateEntry(
                                        p.productId(),
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        p.selectedOptions()))
                .toList();
    }
}
