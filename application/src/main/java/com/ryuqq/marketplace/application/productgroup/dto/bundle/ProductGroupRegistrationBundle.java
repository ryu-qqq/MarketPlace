package com.ryuqq.marketplace.application.productgroup.dto.bundle;

import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionInputType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 상품 그룹 등록 번들.
 *
 * <p>ProductGroup + per-package 등록 데이터를 포함하는 immutable record. per-package 데이터는 productGroupId 없이
 * 생성되며, {@link #bindAll} 메서드로 실제 ID를 바인딩하여 도메인 객체를 생성합니다.
 *
 * <p>Product는 SellerOption persist 이후 이름 기반 resolve가 필요하므로 {@link ProductEntry} 형태로 보관하며,
 * Coordinator에서 별도 처리합니다.
 */
@SuppressWarnings("PMD.ExcessiveImports")
public record ProductGroupRegistrationBundle(
        ProductGroup productGroup,
        List<ImageEntry> images,
        OptionRegistrationData optionData,
        String descriptionContent,
        NoticeRegistrationData noticeData,
        List<ProductEntry> products,
        Instant createdAt) {

    public ProductGroupRegistrationBundle {
        images = List.copyOf(images);
        products = List.copyOf(products);
    }

    // === Preparation Records ===

    /** 이미지 등록 데이터. */
    public record ImageEntry(String imageType, String originUrl, int sortOrder) {}

    /** 옵션 그룹 등록 데이터. */
    public record OptionRegistrationData(OptionType optionType, List<OptionGroupEntry> groups) {

        public OptionRegistrationData {
            groups = List.copyOf(groups);
        }

        /** 옵션 그룹 엔트리. */
        public record OptionGroupEntry(
                String optionGroupName,
                Long canonicalOptionGroupId,
                String inputType,
                List<OptionValueEntry> optionValues) {

            public OptionGroupEntry {
                optionValues = List.copyOf(optionValues);
            }

            /** 옵션 값 엔트리. */
            public record OptionValueEntry(
                    String optionValueName, Long canonicalOptionValueId, int sortOrder) {}
        }
    }

    /** 고시정보 등록 데이터. */
    public record NoticeRegistrationData(long noticeCategoryId, List<NoticeEntry> entries) {

        public NoticeRegistrationData {
            entries = List.copyOf(entries);
        }

        /** 고시정보 엔트리. */
        public record NoticeEntry(long noticeFieldId, String fieldValue) {}
    }

    /** 상품(SKU) 등록 데이터. 이름 기반 옵션 resolve가 필요하므로 도메인 변환은 Coordinator에서 처리합니다. */
    public record ProductEntry(
            String skuCode,
            int regularPrice,
            int currentPrice,
            int stockQuantity,
            int sortOrder,
            List<SelectedOption> selectedOptions) {

        public ProductEntry {
            selectedOptions = List.copyOf(selectedOptions);
        }
    }

    // === Bound Domain Objects ===

    /** productGroupId 바인딩 후 생성된 도메인 객체 묶음 (Product 제외). */
    @SuppressFBWarnings(
            value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
            justification = "Domain objects are immutable by design")
    public record BoundDomainObjects(
            ProductGroupImages images,
            SellerOptionGroups optionGroups,
            OptionType optionType,
            ProductGroupDescription description,
            ProductNotice notice) {}

    /**
     * 이미지, 옵션, 상세설명, 고시정보를 도메인 객체로 변환합니다.
     *
     * @param productGroupId 확정된 상품 그룹 ID
     * @return 도메인 객체 묶음
     */
    public BoundDomainObjects bindAll(ProductGroupId productGroupId) {
        return new BoundDomainObjects(
                createImages(productGroupId),
                createOptionGroups(productGroupId),
                optionData.optionType(),
                createDescription(productGroupId),
                noticeData != null ? createNotice(productGroupId) : null);
    }

    // === Private Domain Creation Methods ===

    private ProductGroupImages createImages(ProductGroupId productGroupId) {
        List<ProductGroupImage> imageList =
                images.stream()
                        .map(
                                img ->
                                        ProductGroupImage.forNew(
                                                productGroupId,
                                                ImageUrl.of(img.originUrl()),
                                                ImageType.valueOf(img.imageType()),
                                                img.sortOrder()))
                        .toList();
        return ProductGroupImages.of(imageList);
    }

    private SellerOptionGroups createOptionGroups(ProductGroupId productGroupId) {
        List<SellerOptionGroup> result = new ArrayList<>();
        int groupSortOrder = 0;

        for (OptionRegistrationData.OptionGroupEntry group : optionData.groups()) {
            SellerOptionGroupId tempGroupId = SellerOptionGroupId.forNew();
            OptionGroupName groupName = OptionGroupName.of(group.optionGroupName());
            OptionInputType inputType =
                    group.inputType() != null
                            ? OptionInputType.valueOf(group.inputType())
                            : OptionInputType.PREDEFINED;

            List<SellerOptionValue> optionValues =
                    group.optionValues().stream()
                            .map(
                                    v -> {
                                        OptionValueName valueName =
                                                OptionValueName.of(v.optionValueName());
                                        if (v.canonicalOptionValueId() != null) {
                                            return SellerOptionValue.forNewWithCanonical(
                                                    tempGroupId,
                                                    valueName,
                                                    CanonicalOptionValueId.of(
                                                            v.canonicalOptionValueId()),
                                                    v.sortOrder());
                                        }
                                        return SellerOptionValue.forNew(
                                                tempGroupId, valueName, v.sortOrder());
                                    })
                            .toList();

            SellerOptionGroup optionGroup;
            if (group.canonicalOptionGroupId() != null) {
                optionGroup =
                        SellerOptionGroup.forNewWithCanonical(
                                productGroupId,
                                groupName,
                                CanonicalOptionGroupId.of(group.canonicalOptionGroupId()),
                                inputType,
                                groupSortOrder++,
                                optionValues);
            } else {
                optionGroup =
                        SellerOptionGroup.forNew(
                                productGroupId,
                                groupName,
                                inputType,
                                groupSortOrder++,
                                optionValues);
            }
            result.add(optionGroup);
        }

        return SellerOptionGroups.of(result);
    }

    private ProductGroupDescription createDescription(ProductGroupId productGroupId) {
        return ProductGroupDescription.forNew(
                productGroupId, DescriptionHtml.of(descriptionContent), createdAt);
    }

    private ProductNotice createNotice(ProductGroupId productGroupId) {
        List<ProductNoticeEntry> noticeEntries =
                noticeData.entries().stream()
                        .map(
                                e ->
                                        ProductNoticeEntry.forNew(
                                                NoticeFieldId.of(e.noticeFieldId()),
                                                NoticeFieldValue.of(e.fieldValue())))
                        .toList();
        return ProductNotice.forNew(
                productGroupId,
                NoticeCategoryId.of(noticeData.noticeCategoryId()),
                noticeEntries,
                createdAt);
    }
}
