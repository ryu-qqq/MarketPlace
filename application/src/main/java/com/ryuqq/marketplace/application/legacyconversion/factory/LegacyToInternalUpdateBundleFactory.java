package com.ryuqq.marketplace.application.legacyconversion.factory;

import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand.ImageCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand.OptionGroupCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand.OptionValueCommand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 데이터를 내부 상품 수정 번들로 변환하는 Factory.
 *
 * <p>LegacyProductGroupDetailBundle + SKU 매핑 → ProductGroupUpdateBundle 변환을 담당합니다. 이미 변환 완료된 상품의
 * 업데이트 동기화에 사용됩니다.
 */
@Component
public class LegacyToInternalUpdateBundleFactory {

    /**
     * 레거시 데이터와 기존 SKU 매핑을 기반으로 내부 수정 번들을 생성합니다.
     *
     * @param legacyBundle 레거시 상품 상세 번들 (최신 데이터)
     * @param internalProductGroupId 내부 상품그룹 ID
     * @param skuMappings 기존 SKU 매핑 목록
     * @param shippingPolicyId 배송 정책 ID
     * @param refundPolicyId 환불 정책 ID
     * @param now 현재 시각
     * @return 내부 상품 수정 번들
     */
    public ProductGroupUpdateBundle create(
            LegacyProductGroupDetailBundle legacyBundle,
            long internalProductGroupId,
            List<LegacyProductIdMapping> skuMappings,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            Instant now) {

        LegacyProductGroupCompositeResult composite = legacyBundle.composite();
        ProductGroupId pgId = ProductGroupId.of(internalProductGroupId);

        ProductGroupUpdateData basicInfo =
                createBasicInfoUpdateData(composite, pgId, shippingPolicyId, refundPolicyId, now);
        UpdateProductGroupImagesCommand imageCommand =
                createImageCommand(internalProductGroupId, composite.images());
        UpdateSellerOptionGroupsCommand optionCommand =
                createOptionCommand(internalProductGroupId, composite, legacyBundle.products());
        UpdateProductGroupDescriptionCommand descriptionCommand =
                new UpdateProductGroupDescriptionCommand(
                        internalProductGroupId, composite.detailDescription());
        UpdateProductNoticeCommand noticeCommand = createNoticeCommand(internalProductGroupId);
        List<ProductDiffUpdateEntry> productEntries =
                createProductEntries(legacyBundle.products(), skuMappings, composite);

        return new ProductGroupUpdateBundle(
                basicInfo,
                imageCommand,
                optionCommand,
                descriptionCommand,
                noticeCommand,
                productEntries);
    }

    private ProductGroupUpdateData createBasicInfoUpdateData(
            LegacyProductGroupCompositeResult composite,
            ProductGroupId pgId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            Instant now) {
        return ProductGroupUpdateData.of(
                pgId,
                ProductGroupName.of(composite.productGroupName()),
                BrandId.of(composite.brandId()),
                CategoryId.of(composite.categoryId()),
                shippingPolicyId,
                refundPolicyId,
                now);
    }

    private UpdateProductGroupImagesCommand createImageCommand(
            long internalProductGroupId, List<LegacyProductGroupCompositeResult.ImageInfo> images) {
        if (images == null || images.isEmpty()) {
            return new UpdateProductGroupImagesCommand(internalProductGroupId, List.of());
        }
        List<ImageCommand> commands = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            LegacyProductGroupCompositeResult.ImageInfo img = images.get(i);
            commands.add(new ImageCommand(img.imageType(), img.imageUrl(), i + 1));
        }
        return new UpdateProductGroupImagesCommand(internalProductGroupId, commands);
    }

    private UpdateSellerOptionGroupsCommand createOptionCommand(
            long internalProductGroupId,
            LegacyProductGroupCompositeResult composite,
            List<LegacyProductCompositeResult> products) {

        if (products.isEmpty()) {
            return new UpdateSellerOptionGroupsCommand(internalProductGroupId, List.of());
        }

        Map<String, Set<String>> groupValueMap = new LinkedHashMap<>();
        for (LegacyProductCompositeResult product : products) {
            for (LegacyProductCompositeResult.OptionMapping mapping : product.optionMappings()) {
                groupValueMap
                        .computeIfAbsent(mapping.optionGroupName(), k -> new LinkedHashSet<>())
                        .add(mapping.optionValue());
            }
        }

        List<OptionGroupCommand> groups = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : groupValueMap.entrySet()) {
            AtomicInteger sortOrder = new AtomicInteger(0);
            List<OptionValueCommand> values =
                    entry.getValue().stream()
                            .map(
                                    v ->
                                            new OptionValueCommand(
                                                    null, v, null, sortOrder.getAndIncrement()))
                            .toList();
            groups.add(new OptionGroupCommand(null, entry.getKey(), null, "PREDEFINED", values));
        }

        return new UpdateSellerOptionGroupsCommand(internalProductGroupId, groups);
    }

    /** 고시정보는 레거시↔내부 구조 차이로 현재 빈 Command를 생성합니다. */
    private UpdateProductNoticeCommand createNoticeCommand(long internalProductGroupId) {
        return new UpdateProductNoticeCommand(internalProductGroupId, 0L, List.of());
    }

    /**
     * 레거시 상품 목록을 내부 Product diff 엔트리로 변환합니다.
     *
     * <p>기존 SKU 매핑이 있으면 productId를 채워 retained(수정)로, 없으면 null로 채워 added(신규)로 처리됩니다.
     */
    private List<ProductDiffUpdateEntry> createProductEntries(
            List<LegacyProductCompositeResult> legacyProducts,
            List<LegacyProductIdMapping> skuMappings,
            LegacyProductGroupCompositeResult composite) {

        Map<Long, Long> legacyToInternalMap =
                skuMappings.stream()
                        .collect(
                                Collectors.toMap(
                                        LegacyProductIdMapping::legacyProductId,
                                        LegacyProductIdMapping::internalProductId));

        List<ProductDiffUpdateEntry> entries = new ArrayList<>();
        for (int i = 0; i < legacyProducts.size(); i++) {
            LegacyProductCompositeResult product = legacyProducts.get(i);
            Long internalProductId = legacyToInternalMap.get(product.productId());

            List<SelectedOption> selectedOptions =
                    product.optionMappings().stream()
                            .map(m -> new SelectedOption(m.optionGroupName(), m.optionValue()))
                            .toList();

            entries.add(
                    new ProductDiffUpdateEntry(
                            internalProductId,
                            null,
                            (int) composite.regularPrice(),
                            (int) composite.currentPrice(),
                            product.stockQuantity(),
                            i + 1,
                            selectedOptions));
        }
        return entries;
    }
}
