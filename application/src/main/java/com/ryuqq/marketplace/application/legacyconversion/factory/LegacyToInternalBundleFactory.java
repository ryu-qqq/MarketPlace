package com.ryuqq.marketplace.application.legacyconversion.factory;

import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.application.legacyconversion.internal.LegacyConversionResolvedContext;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionInputType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 데이터를 내부 상품 등록 번들로 변환하는 Factory.
 *
 * <p>LegacyProductGroupDetailBundle → ProductGroupRegistrationBundle 변환을 담당합니다.
 *
 * <p>고시정보는 레거시 고정 컬럼(MATERIAL, COLOR 등)을 내부 notice_field의 field_code로 매핑하여 변환합니다. 매핑되지 않는 필드는 "상세설명
 * 참고" 기본값으로 채웁니다.
 */
@Component
public class LegacyToInternalBundleFactory {

    private static final Logger log = LoggerFactory.getLogger(LegacyToInternalBundleFactory.class);
    private static final String DEFAULT_NOTICE_VALUE = "상세설명 참고";
    private static final String DEFAULT_THUMBNAIL_URL =
            "https://cdn.set-of.com/public/logo/setof_logo.jpg";

    /**
     * 레거시 상품 번들을 내부 등록 번들로 변환합니다.
     *
     * @param legacyBundle 레거시 상품 상세 번들
     * @param resolvedContext 사전 해소된 내부 ID 컨텍스트
     * @param now 현재 시각
     * @return 내부 상품 등록 번들
     */
    public ProductGroupRegistrationBundle create(
            LegacyProductGroupDetailBundle legacyBundle,
            LegacyConversionResolvedContext resolvedContext,
            Instant now) {

        LegacyProductGroupCompositeResult composite = legacyBundle.composite();
        OptionType optionType =
                com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType.valueOf(
                                composite.optionType())
                        .toInternalOptionType();

        List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups =
                convertOptionGroups(composite, legacyBundle.products(), optionType);

        optionType = adjustOptionType(optionType, optionGroups.size(), composite.productGroupId());

        ProductGroup productGroup = createProductGroup(composite, resolvedContext, optionType, now);

        long noticeCategoryId =
                resolvedContext.noticeCategory().map(NoticeCategory::idValue).orElse(0L);
        List<RegisterProductNoticeCommand.NoticeEntryCommand> noticeEntries =
                convertNoticeEntries(
                        composite.notice(), resolvedContext.noticeCategory().orElse(null));

        return new ProductGroupRegistrationBundle(
                productGroup,
                convertImages(composite.images()),
                optionType.name(),
                optionGroups,
                composite.detailDescription(),
                noticeCategoryId,
                noticeEntries,
                convertProducts(legacyBundle.products(), composite),
                now);
    }

    private OptionType adjustOptionType(
            OptionType original, int actualGroupCount, long legacyProductGroupId) {
        int expectedCount =
                switch (original) {
                    case NONE -> 0;
                    case SINGLE -> 1;
                    case COMBINATION -> 2;
                };
        if (expectedCount == actualGroupCount) {
            return original;
        }
        OptionType adjusted =
                switch (actualGroupCount) {
                    case 0 -> OptionType.NONE;
                    case 1 -> OptionType.SINGLE;
                    default -> OptionType.COMBINATION;
                };
        log.warn(
                "옵션 타입 불일치 보정: legacyProductGroupId={}, original={}, actualGroupCount={},"
                        + " adjusted={}",
                legacyProductGroupId,
                original,
                actualGroupCount,
                adjusted);
        return adjusted;
    }

    private ProductGroup createProductGroup(
            LegacyProductGroupCompositeResult composite,
            LegacyConversionResolvedContext resolvedContext,
            OptionType optionType,
            Instant now) {
        return ProductGroup.forNew(
                resolvedContext.sellerId(),
                resolvedContext.brandId(),
                resolvedContext.categoryId(),
                resolvedContext.shippingPolicyId(),
                resolvedContext.refundPolicyId(),
                ProductGroupName.of(composite.productGroupName()),
                optionType,
                now);
    }

    private List<RegisterProductGroupImagesCommand.ImageCommand> convertImages(
            List<LegacyProductGroupCompositeResult.ImageInfo> images) {
        if (images == null || images.isEmpty()) {
            return List.of(
                    new RegisterProductGroupImagesCommand.ImageCommand(
                            ImageType.THUMBNAIL.name(), DEFAULT_THUMBNAIL_URL, 1));
        }

        List<RegisterProductGroupImagesCommand.ImageCommand> commands = new ArrayList<>();
        boolean thumbnailAdded = false;
        int sortOrder = 1;

        for (LegacyProductGroupCompositeResult.ImageInfo img : images) {
            String imageType = toLegacyImageType(img.imageType());
            if (ImageType.THUMBNAIL.name().equals(imageType)) {
                if (thumbnailAdded) {
                    imageType = ImageType.DETAIL.name();
                } else {
                    thumbnailAdded = true;
                }
            }
            commands.add(
                    new RegisterProductGroupImagesCommand.ImageCommand(
                            imageType, img.imageUrl(), sortOrder++));
        }

        if (!thumbnailAdded) {
            commands.addFirst(
                    new RegisterProductGroupImagesCommand.ImageCommand(
                            ImageType.THUMBNAIL.name(), DEFAULT_THUMBNAIL_URL, 0));
        }

        return commands;
    }

    private static String toLegacyImageType(String legacyType) {
        if ("MAIN".equals(legacyType)) {
            return ImageType.THUMBNAIL.name();
        }
        return legacyType;
    }

    private List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> convertOptionGroups(
            LegacyProductGroupCompositeResult composite,
            List<LegacyProductCompositeResult> products,
            OptionType optionType) {

        if (products.isEmpty()) {
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
                            entry.getKey(), null, OptionInputType.PREDEFINED.name(), values));
        }

        return groups;
    }

    /**
     * 레거시 NoticeInfo를 내부 NoticeEntryCommand 목록으로 변환합니다.
     *
     * <p>NoticeCategory의 모든 필드를 순회하며, 레거시 데이터에 매핑되는 값이 있으면 해당 값을, 없으면 "상세설명 참고" 기본값을 사용합니다. 카테고리에
     * 존재하지 않는 field_code는 무시합니다.
     */
    private List<RegisterProductNoticeCommand.NoticeEntryCommand> convertNoticeEntries(
            LegacyProductGroupCompositeResult.NoticeInfo noticeInfo,
            NoticeCategory noticeCategory) {
        if (noticeCategory == null) {
            return List.of();
        }

        Map<String, String> legacyValues =
                LegacyNoticeFieldMapper.extractLegacyValues(noticeInfo, noticeCategory);
        List<RegisterProductNoticeCommand.NoticeEntryCommand> entries = new ArrayList<>();

        for (NoticeField field : noticeCategory.fields()) {
            String fieldCode = field.fieldCodeValue();
            String value = legacyValues.getOrDefault(fieldCode, DEFAULT_NOTICE_VALUE);
            entries.add(
                    new RegisterProductNoticeCommand.NoticeEntryCommand(field.idValue(), value));
        }

        return entries;
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
}
