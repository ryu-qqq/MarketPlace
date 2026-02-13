package com.ryuqq.marketplace.application.productgroup.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductCreations;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.ChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupImages;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeEntries;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductGroup Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class ProductGroupCommandFactory {

    private final TimeProvider timeProvider;
    private final ProductGroupImageFactory imageFactory;
    private final SellerOptionGroupFactory optionGroupFactory;
    private final ProductCreationDataFactory productCreationDataFactory;
    private final ProductNoticeEntryFactory noticeEntryFactory;

    public ProductGroupCommandFactory(
            TimeProvider timeProvider,
            ProductGroupImageFactory imageFactory,
            SellerOptionGroupFactory optionGroupFactory,
            ProductCreationDataFactory productCreationDataFactory,
            ProductNoticeEntryFactory noticeEntryFactory) {
        this.timeProvider = timeProvider;
        this.imageFactory = imageFactory;
        this.optionGroupFactory = optionGroupFactory;
        this.productCreationDataFactory = productCreationDataFactory;
        this.noticeEntryFactory = noticeEntryFactory;
    }

    /** 상태 변경 컨텍스트 생성. */
    public StatusChangeContext<ProductGroupId> createStatusChangeContext(
            ChangeProductGroupStatusCommand command) {
        return new StatusChangeContext<>(
                ProductGroupId.of(command.productGroupId()), timeProvider.now());
    }

    /** 기본 정보 수정 컨텍스트 생성. */
    public UpdateContext<ProductGroupId, UpdateProductGroupBasicInfoCommand>
            createBasicInfoUpdateContext(UpdateProductGroupBasicInfoCommand command) {
        return new UpdateContext<>(
                ProductGroupId.of(command.productGroupId()), command, timeProvider.now());
    }

    /** 이미지 수정 컨텍스트 생성 (이미지 리스트 변환 포함). */
    public UpdateContext<ProductGroupId, ProductGroupImages> createImagesUpdateContext(
            UpdateProductGroupImagesCommand command) {
        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());
        ProductGroupImages images =
                imageFactory.create(
                        productGroupId, toImageDataListForImageUpdate(command.images()));
        return new UpdateContext<>(productGroupId, images, timeProvider.now());
    }

    /**
     * 상품 그룹 등록 번들 생성.
     *
     * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
     *
     * @param command 등록 Command
     * @return 등록 번들
     */
    public ProductGroupRegistrationBundle createRegistrationBundle(
            RegisterProductGroupCommand command) {
        Instant now = timeProvider.now();

        // 1. 이미지 생성
        ProductGroupImages images =
                imageFactory.create(ProductGroupId.forNew(), toImageDataList(command.images()));

        // 2. SellerOptionGroup 생성
        SellerOptionGroups optionGroups =
                optionGroupFactory.create(
                        ProductGroupId.forNew(), toOptionGroupDataList(command.optionGroups()));

        // 3. ProductGroup 생성
        ProductGroup productGroup =
                ProductGroup.forNew(
                        SellerId.of(command.sellerId()),
                        BrandId.of(command.brandId()),
                        CategoryId.of(command.categoryId()),
                        ShippingPolicyId.of(command.shippingPolicyId()),
                        RefundPolicyId.of(command.refundPolicyId()),
                        ProductGroupName.of(command.productGroupName()),
                        OptionType.valueOf(command.optionType()),
                        images,
                        optionGroups,
                        now);

        // 4. Product 생성 데이터 준비
        ProductCreations productCreations =
                productCreationDataFactory.create(
                        toProductDataList(command.products()), optionGroups);

        // 5. Description 변환
        DescriptionHtml descriptionContent = toDescriptionHtml(command.description());

        // 6. DescriptionImage 변환
        List<DescriptionImage> descriptionImages =
                toDescriptionImages(command.description().descriptionImages());

        // 7. Notice 변환
        ProductNoticeEntries noticeEntries =
                noticeEntryFactory.create(toNoticeData(command.notice()));

        return new ProductGroupRegistrationBundle(
                productGroup,
                descriptionContent,
                descriptionImages,
                noticeEntries,
                productCreations,
                now);
    }

    /**
     * 상품 그룹 수정 번들 생성.
     *
     * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
     *
     * @param command 수정 Command
     * @return 수정 번들
     */
    public ProductGroupUpdateBundle createUpdateBundle(UpdateProductGroupFullCommand command) {
        Instant now = timeProvider.now();

        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());

        // 1. 이미지 생성
        ProductGroupImages images =
                imageFactory.create(productGroupId, toImageDataListForUpdate(command.images()));

        // 2. SellerOptionGroup 생성
        SellerOptionGroups optionGroups =
                optionGroupFactory.create(
                        productGroupId, toOptionGroupDataListForUpdate(command.optionGroups()));

        // 3. Product 생성 데이터 준비
        ProductCreations productCreations =
                productCreationDataFactory.create(
                        toProductDataListForUpdate(command.products()), optionGroups);

        // 4. Description 변환
        DescriptionHtml descriptionContent = toDescriptionHtmlForUpdate(command.description());

        // 5. DescriptionImage 변환
        List<DescriptionImage> descriptionImages =
                toDescriptionImagesForUpdate(command.description().descriptionImages());

        // 6. Notice 변환
        ProductNoticeEntries noticeEntries =
                noticeEntryFactory.create(toNoticeDataForUpdate(command.notice()));

        return new ProductGroupUpdateBundle(
                productGroupId,
                ProductGroupName.of(command.productGroupName()),
                BrandId.of(command.brandId()),
                CategoryId.of(command.categoryId()),
                ShippingPolicyId.of(command.shippingPolicyId()),
                RefundPolicyId.of(command.refundPolicyId()),
                images,
                optionGroups,
                descriptionContent,
                descriptionImages,
                noticeEntries,
                productCreations,
                now);
    }

    // === Command → intermediate data 변환 ===

    private List<ProductGroupImageFactory.ImageData> toImageDataList(
            List<RegisterProductGroupCommand.ImageCommand> images) {
        return images.stream()
                .map(
                        img ->
                                new ProductGroupImageFactory.ImageData(
                                        img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private List<ProductGroupImageFactory.ImageData> toImageDataListForUpdate(
            List<UpdateProductGroupFullCommand.ImageCommand> images) {
        return images.stream()
                .map(
                        img ->
                                new ProductGroupImageFactory.ImageData(
                                        img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private List<ProductGroupImageFactory.ImageData> toImageDataListForImageUpdate(
            List<UpdateProductGroupImagesCommand.ImageCommand> images) {
        return images.stream()
                .map(
                        img ->
                                new ProductGroupImageFactory.ImageData(
                                        img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private List<SellerOptionGroupFactory.OptionGroupData> toOptionGroupDataList(
            List<RegisterProductGroupCommand.OptionGroupCommand> optionGroups) {
        return optionGroups.stream()
                .map(
                        group -> {
                            List<SellerOptionGroupFactory.OptionValueData> valueDataList =
                                    group.optionValues().stream()
                                            .map(
                                                    v ->
                                                            new SellerOptionGroupFactory
                                                                    .OptionValueData(
                                                                    v.optionValueName(),
                                                                    v.canonicalOptionValueId(),
                                                                    v.sortOrder()))
                                            .toList();
                            return new SellerOptionGroupFactory.OptionGroupData(
                                    group.optionGroupName(),
                                    group.canonicalOptionGroupId(),
                                    valueDataList);
                        })
                .toList();
    }

    private List<SellerOptionGroupFactory.OptionGroupData> toOptionGroupDataListForUpdate(
            List<UpdateProductGroupFullCommand.OptionGroupCommand> optionGroups) {
        return optionGroups.stream()
                .map(
                        group -> {
                            List<SellerOptionGroupFactory.OptionValueData> valueDataList =
                                    group.optionValues().stream()
                                            .map(
                                                    v ->
                                                            new SellerOptionGroupFactory
                                                                    .OptionValueData(
                                                                    v.optionValueName(),
                                                                    v.canonicalOptionValueId(),
                                                                    v.sortOrder()))
                                            .toList();
                            return new SellerOptionGroupFactory.OptionGroupData(
                                    group.optionGroupName(),
                                    group.canonicalOptionGroupId(),
                                    valueDataList);
                        })
                .toList();
    }

    private List<ProductCreationDataFactory.ProductData> toProductDataList(
            List<RegisterProductGroupCommand.ProductCommand> products) {
        return products.stream()
                .map(
                        p ->
                                new ProductCreationDataFactory.ProductData(
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.salePrice(),
                                        p.discountRate(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        p.optionIndices()))
                .toList();
    }

    private List<ProductCreationDataFactory.ProductData> toProductDataListForUpdate(
            List<UpdateProductGroupFullCommand.ProductCommand> products) {
        return products.stream()
                .map(
                        p ->
                                new ProductCreationDataFactory.ProductData(
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.salePrice(),
                                        p.discountRate(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        p.optionIndices()))
                .toList();
    }

    private DescriptionHtml toDescriptionHtml(
            RegisterProductGroupCommand.DescriptionCommand description) {
        return DescriptionHtml.of(description.content());
    }

    private DescriptionHtml toDescriptionHtmlForUpdate(
            UpdateProductGroupFullCommand.DescriptionCommand description) {
        return DescriptionHtml.of(description.content());
    }

    private List<DescriptionImage> toDescriptionImages(
            List<RegisterProductGroupCommand.DescriptionImageCommand> descriptionImages) {
        if (descriptionImages == null || descriptionImages.isEmpty()) {
            return List.of();
        }
        return descriptionImages.stream()
                .map(img -> DescriptionImage.forNew(ImageUrl.of(img.originUrl()), img.sortOrder()))
                .toList();
    }

    private List<DescriptionImage> toDescriptionImagesForUpdate(
            List<UpdateProductGroupFullCommand.DescriptionImageCommand> descriptionImages) {
        if (descriptionImages == null || descriptionImages.isEmpty()) {
            return List.of();
        }
        return descriptionImages.stream()
                .map(img -> DescriptionImage.forNew(ImageUrl.of(img.originUrl()), img.sortOrder()))
                .toList();
    }

    private ProductNoticeEntryFactory.NoticeData toNoticeData(
            RegisterProductGroupCommand.NoticeCommand notice) {
        List<ProductNoticeEntryFactory.NoticeEntryData> entryDataList =
                notice.entries().stream()
                        .map(
                                entry ->
                                        new ProductNoticeEntryFactory.NoticeEntryData(
                                                entry.noticeFieldId(), entry.fieldValue()))
                        .toList();
        return new ProductNoticeEntryFactory.NoticeData(notice.noticeCategoryId(), entryDataList);
    }

    private ProductNoticeEntryFactory.NoticeData toNoticeDataForUpdate(
            UpdateProductGroupFullCommand.NoticeCommand notice) {
        List<ProductNoticeEntryFactory.NoticeEntryData> entryDataList =
                notice.entries().stream()
                        .map(
                                entry ->
                                        new ProductNoticeEntryFactory.NoticeEntryData(
                                                entry.noticeFieldId(), entry.fieldValue()))
                        .toList();
        return new ProductNoticeEntryFactory.NoticeData(notice.noticeCategoryId(), entryDataList);
    }
}
