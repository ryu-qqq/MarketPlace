package com.ryuqq.marketplace.application.legacyconversion.factory;

import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 데이터를 내부 상품 등록 번들로 변환하는 Factory.
 *
 * <p>LegacyProductGroupDetailBundle → ProductGroupRegistrationBundle 변환을 담당합니다.
 *
 * <p>고시정보(Notice)는 레거시 시스템의 고정 필드 구조와 내부 시스템의 동적 구조가 달라 현재 변환에서 제외됩니다.
 */
@Component
public class LegacyToInternalBundleFactory {

    /**
     * 레거시 상품 번들을 내부 등록 번들로 변환합니다.
     *
     * @param legacyBundle 레거시 상품 상세 번들
     * @param shippingPolicyId 셀러의 배송 정책 ID
     * @param refundPolicyId 셀러의 환불 정책 ID
     * @param now 현재 시각
     * @return 내부 상품 등록 번들
     */
    public ProductGroupRegistrationBundle create(
            LegacyProductGroupDetailBundle legacyBundle,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            Instant now) {

        LegacyProductGroupCompositeResult composite = legacyBundle.composite();
        OptionType optionType = mapOptionType(composite.optionType());

        ProductGroup productGroup =
                createProductGroup(composite, shippingPolicyId, refundPolicyId, optionType, now);

        return new ProductGroupRegistrationBundle(
                productGroup,
                convertImages(composite.images()),
                optionType.name(),
                convertOptionGroups(composite, legacyBundle.products(), optionType),
                composite.detailDescription(),
                0L,
                List.of(),
                convertProducts(legacyBundle.products(), composite),
                now);
    }

    private ProductGroup createProductGroup(
            LegacyProductGroupCompositeResult composite,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            OptionType optionType,
            Instant now) {
        return ProductGroup.forNew(
                SellerId.of(composite.sellerId()),
                BrandId.of(composite.brandId()),
                CategoryId.of(composite.categoryId()),
                shippingPolicyId,
                refundPolicyId,
                ProductGroupName.of(composite.productGroupName()),
                optionType,
                now);
    }

    private List<RegisterProductGroupImagesCommand.ImageCommand> convertImages(
            List<LegacyProductGroupCompositeResult.ImageInfo> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        List<RegisterProductGroupImagesCommand.ImageCommand> commands = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            LegacyProductGroupCompositeResult.ImageInfo img = images.get(i);
            commands.add(
                    new RegisterProductGroupImagesCommand.ImageCommand(
                            img.imageType(), img.imageUrl(), i + 1));
        }
        return commands;
    }

    private List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> convertOptionGroups(
            LegacyProductGroupCompositeResult composite,
            List<LegacyProductCompositeResult> products,
            OptionType optionType) {

        if (!optionType.requiresOptionGroup() || products.isEmpty()) {
            return List.of();
        }

        Map<String, Set<String>> groupValueMap = new LinkedHashMap<>();
        for (LegacyProductCompositeResult product : products) {
            for (LegacyProductCompositeResult.OptionMapping mapping : product.optionMappings()) {
                groupValueMap
                        .computeIfAbsent(mapping.optionGroupName(), k -> new LinkedHashSet<>())
                        .add(mapping.optionValue());
            }
        }

        List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> groups = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : groupValueMap.entrySet()) {
            AtomicInteger sortOrder = new AtomicInteger(0);
            List<RegisterSellerOptionGroupsCommand.OptionValueCommand> values =
                    entry.getValue().stream()
                            .map(
                                    v ->
                                            new RegisterSellerOptionGroupsCommand
                                                    .OptionValueCommand(
                                                    v, null, sortOrder.getAndIncrement()))
                            .toList();
            groups.add(
                    new RegisterSellerOptionGroupsCommand.OptionGroupCommand(
                            entry.getKey(), null, "PREDEFINED", values));
        }

        return groups;
    }

    private List<RegisterProductsCommand.ProductData> convertProducts(
            List<LegacyProductCompositeResult> products,
            LegacyProductGroupCompositeResult composite) {
        if (products.isEmpty()) {
            return List.of();
        }

        List<RegisterProductsCommand.ProductData> entries = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            LegacyProductCompositeResult product = products.get(i);
            List<SelectedOption> selectedOptions =
                    product.optionMappings().stream()
                            .map(m -> new SelectedOption(m.optionGroupName(), m.optionValue()))
                            .toList();

            entries.add(
                    new RegisterProductsCommand.ProductData(
                            null,
                            (int) composite.regularPrice(),
                            (int) composite.currentPrice(),
                            product.stockQuantity(),
                            i + 1,
                            selectedOptions));
        }
        return entries;
    }

    /**
     * 레거시 옵션 타입을 내부 옵션 타입으로 매핑합니다.
     *
     * <p>Legacy SINGLE → Internal NONE (옵션 없음), Legacy OPTION_ONE → Internal SINGLE, Legacy
     * OPTION_TWO → Internal COMBINATION.
     */
    private OptionType mapOptionType(String legacyOptionType) {
        return switch (legacyOptionType) {
            case "SINGLE" -> OptionType.NONE;
            case "OPTION_ONE" -> OptionType.SINGLE;
            case "OPTION_TWO" -> OptionType.COMBINATION;
            default -> throw new IllegalArgumentException("지원하지 않는 레거시 옵션 타입: " + legacyOptionType);
        };
    }
}
