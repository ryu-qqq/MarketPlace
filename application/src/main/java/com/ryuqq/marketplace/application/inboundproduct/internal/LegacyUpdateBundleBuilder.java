package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.dto.payload.LegacyInboundUpdatePayload;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.LegacyInboundUpdatePayload.LegacyUpdateStatus;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyNoticeCategoryResolver;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 레거시 업데이트 payload를 ProductGroupUpdateBundle로 빌드하는 컴포넌트. */
@SuppressWarnings("PMD.ExcessiveImports")
@Component
public class LegacyUpdateBundleBuilder {

    private static final Logger log = LoggerFactory.getLogger(LegacyUpdateBundleBuilder.class);
    private static final long DEFAULT_POLICY_ID = 1L;

    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final RefundPolicyReadManager refundPolicyReadManager;
    private final LegacyNoticeCategoryResolver legacyNoticeCategoryResolver;

    public LegacyUpdateBundleBuilder(
            ShippingPolicyReadManager shippingPolicyReadManager,
            RefundPolicyReadManager refundPolicyReadManager,
            LegacyNoticeCategoryResolver legacyNoticeCategoryResolver) {
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
        this.legacyNoticeCategoryResolver = legacyNoticeCategoryResolver;
    }

    public Optional<ProductGroupUpdateBundle> build(
            InboundProduct product, LegacyInboundUpdatePayload payload) {
        if (product.internalProductGroupId() == null
                || product.internalBrandId() == null
                || product.internalCategoryId() == null) {
            log.warn("레거시 업데이트 번들 생성 스킵: 내부 매핑/변환 정보 없음. inboundProductId={}", product.idValue());
            return Optional.empty();
        }

        LegacyUpdateStatus updateStatus = payload.updateStatus();
        if (updateStatus == null) {
            log.warn("레거시 업데이트 번들 생성 스킵: updateStatus 없음. inboundProductId={}", product.idValue());
            return Optional.empty();
        }

        ProductGroupUpdateData basicInfoUpdateData = buildBasicInfoUpdateData(product);
        UpdateProductGroupImagesCommand imageCommand =
                updateStatus.imageStatus() ? buildUpdateImageCommand(payload, product) : null;
        UpdateSellerOptionGroupsCommand optionGroupCommand =
                updateStatus.stockOptionStatus()
                        ? buildUpdateOptionGroupCommand(payload, product)
                        : null;
        UpdateProductGroupDescriptionCommand descriptionCommand =
                updateStatus.descriptionStatus()
                        ? buildUpdateDescriptionCommand(payload, product)
                        : null;
        UpdateProductNoticeCommand noticeCommand =
                updateStatus.noticeStatus() ? buildUpdateNoticeCommand(payload, product) : null;
        List<ProductDiffUpdateEntry> productEntries =
                updateStatus.stockOptionStatus() ? buildUpdateProductEntries(payload) : List.of();

        return Optional.of(
                new ProductGroupUpdateBundle(
                        basicInfoUpdateData,
                        imageCommand,
                        optionGroupCommand,
                        descriptionCommand,
                        noticeCommand,
                        productEntries));
    }

    private ProductGroupUpdateData buildBasicInfoUpdateData(InboundProduct product) {
        SellerId sellerId = SellerId.of(product.sellerId());
        return ProductGroupUpdateData.of(
                ProductGroupId.of(product.internalProductGroupId()),
                ProductGroupName.of(product.productName()),
                BrandId.of(product.internalBrandId()),
                CategoryId.of(product.internalCategoryId()),
                resolveShippingPolicyId(sellerId),
                resolveRefundPolicyId(sellerId),
                product.updatedAt());
    }

    private UpdateProductGroupImagesCommand buildUpdateImageCommand(
            LegacyInboundUpdatePayload payload, InboundProduct product) {
        List<UpdateProductGroupImagesCommand.ImageCommand> images = new ArrayList<>();
        int sortOrder = 1;
        for (var img : payload.productImageList()) {
            String originUrl = img.originUrl() != null ? img.originUrl() : img.productImageUrl();
            images.add(
                    new UpdateProductGroupImagesCommand.ImageCommand(
                            img.type(), originUrl, sortOrder++));
        }
        return new UpdateProductGroupImagesCommand(product.internalProductGroupId(), images);
    }

    private UpdateProductGroupDescriptionCommand buildUpdateDescriptionCommand(
            LegacyInboundUpdatePayload payload, InboundProduct product) {
        String content =
                payload.detailDescription() != null
                        ? payload.detailDescription().detailDescription()
                        : "";
        return new UpdateProductGroupDescriptionCommand(product.internalProductGroupId(), content);
    }

    private UpdateProductNoticeCommand buildUpdateNoticeCommand(
            LegacyInboundUpdatePayload payload, InboundProduct product) {
        long noticeCategoryId =
                legacyNoticeCategoryResolver
                        .resolveByProductGroupId(product.internalProductGroupId())
                        .id()
                        .value();
        List<UpdateProductNoticeCommand.NoticeEntryCommand> entries = new ArrayList<>();
        var notice = payload.productNotice();
        if (notice != null) {
            // TODO: fieldCode 기반 매핑으로 전환 (의류 고시 필드 코드: material, color, size 등)
            addUpdateNoticeEntry(entries, 1L, notice.material());
            addUpdateNoticeEntry(entries, 2L, notice.color());
            addUpdateNoticeEntry(entries, 3L, notice.size());
            addUpdateNoticeEntry(entries, 4L, notice.maker());
            addUpdateNoticeEntry(entries, 5L, notice.origin());
            addUpdateNoticeEntry(entries, 6L, notice.washingMethod());
            addUpdateNoticeEntry(entries, 7L, notice.yearMonth());
            addUpdateNoticeEntry(entries, 8L, notice.assuranceStandard());
            addUpdateNoticeEntry(entries, 9L, notice.asPhone());
        }
        return new UpdateProductNoticeCommand(
                product.internalProductGroupId(), noticeCategoryId, entries);
    }

    private void addUpdateNoticeEntry(
            List<UpdateProductNoticeCommand.NoticeEntryCommand> entries,
            long fieldId,
            String value) {
        if (value != null && !value.isBlank()) {
            entries.add(new UpdateProductNoticeCommand.NoticeEntryCommand(fieldId, value));
        }
    }

    private UpdateSellerOptionGroupsCommand buildUpdateOptionGroupCommand(
            LegacyInboundUpdatePayload payload, InboundProduct product) {
        Map<String, Map<String, LegacyInboundUpdatePayload.LegacyPayloadOptionDetail>> grouped =
                new LinkedHashMap<>();
        for (var option : payload.productOptions()) {
            for (var detail : option.options()) {
                String groupName = detail.optionName() != null ? detail.optionName() : "기본옵션";
                grouped.computeIfAbsent(groupName, k -> new LinkedHashMap<>())
                        .putIfAbsent(detail.optionValue(), detail);
            }
        }

        List<UpdateSellerOptionGroupsCommand.OptionGroupCommand> optionGroups = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            List<UpdateSellerOptionGroupsCommand.OptionValueCommand> valueCommands =
                    new ArrayList<>();
            int valueSortOrder = 0;
            for (var detail : entry.getValue().values()) {
                valueCommands.add(
                        new UpdateSellerOptionGroupsCommand.OptionValueCommand(
                                detail.optionDetailId(),
                                detail.optionValue(),
                                null,
                                ++valueSortOrder));
            }
            optionGroups.add(
                    new UpdateSellerOptionGroupsCommand.OptionGroupCommand(
                            null, entry.getKey(), null, "PREDEFINED", valueCommands));
        }
        return new UpdateSellerOptionGroupsCommand(product.internalProductGroupId(), optionGroups);
    }

    private List<ProductDiffUpdateEntry> buildUpdateProductEntries(
            LegacyInboundUpdatePayload payload) {
        List<ProductDiffUpdateEntry> entries = new ArrayList<>();
        int sortOrder = 0;
        for (var option : payload.productOptions()) {
            List<SelectedOption> selectedOptions =
                    option.options().stream()
                            .map(d -> new SelectedOption(d.optionName(), d.optionValue()))
                            .toList();
            int additionalPrice =
                    option.additionalPrice() != null ? option.additionalPrice().intValue() : 0;
            entries.add(
                    new ProductDiffUpdateEntry(
                            option.productId(),
                            null,
                            additionalPrice,
                            additionalPrice,
                            option.quantity() != null ? option.quantity() : 0,
                            ++sortOrder,
                            selectedOptions));
        }
        return entries;
    }

    private ShippingPolicyId resolveShippingPolicyId(SellerId sellerId) {
        Optional<ShippingPolicy> defaultPolicy =
                shippingPolicyReadManager.findDefaultBySellerId(sellerId);
        return defaultPolicy.map(ShippingPolicy::id).orElse(ShippingPolicyId.of(DEFAULT_POLICY_ID));
    }

    private RefundPolicyId resolveRefundPolicyId(SellerId sellerId) {
        Optional<RefundPolicy> defaultPolicy =
                refundPolicyReadManager.findDefaultBySellerId(sellerId);
        return defaultPolicy.map(RefundPolicy::id).orElse(RefundPolicyId.of(DEFAULT_POLICY_ID));
    }
}
