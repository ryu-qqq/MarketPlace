package com.ryuqq.marketplace.domain.productgroup;

import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.*;
import com.ryuqq.marketplace.domain.productgroup.id.*;
import com.ryuqq.marketplace.domain.productgroup.vo.*;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.List;

/**
 * ProductGroup 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ProductGroup 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ProductGroupFixtures {

    private ProductGroupFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final Long DEFAULT_BRAND_ID = 100L;
    public static final Long DEFAULT_CATEGORY_ID = 200L;
    public static final Long DEFAULT_SHIPPING_POLICY_ID = 1L;
    public static final Long DEFAULT_REFUND_POLICY_ID = 1L;
    public static final String DEFAULT_PRODUCT_GROUP_NAME = "테스트 상품 그룹";
    public static final String DEFAULT_DESCRIPTION_HTML = "<p>상품 상세설명</p>";
    public static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    public static final String DEFAULT_CDN_PATH = "https://cdn.example.com/products/";
    public static final String DEFAULT_OPTION_GROUP_NAME = "색상";
    public static final String DEFAULT_OPTION_VALUE_NAME = "검정";

    // ===== ID Fixtures =====
    public static ProductGroupId defaultProductGroupId() {
        return ProductGroupId.of(1L);
    }

    public static ProductGroupId productGroupId(Long value) {
        return ProductGroupId.of(value);
    }

    public static ProductGroupId newProductGroupId() {
        return ProductGroupId.forNew();
    }

    public static ProductGroupDescriptionId defaultProductGroupDescriptionId() {
        return ProductGroupDescriptionId.of(1L);
    }

    public static ProductGroupImageId defaultProductGroupImageId() {
        return ProductGroupImageId.of(1L);
    }

    public static DescriptionImageId defaultDescriptionImageId() {
        return DescriptionImageId.of(1L);
    }

    public static SellerOptionGroupId defaultSellerOptionGroupId() {
        return SellerOptionGroupId.of(1L);
    }

    public static SellerOptionValueId defaultSellerOptionValueId() {
        return SellerOptionValueId.of(1L);
    }

    // ===== VO Fixtures =====
    public static ProductGroupName defaultProductGroupName() {
        return ProductGroupName.of(DEFAULT_PRODUCT_GROUP_NAME);
    }

    public static ProductGroupName productGroupName(String value) {
        return ProductGroupName.of(value);
    }

    public static DescriptionHtml defaultDescriptionHtml() {
        return DescriptionHtml.of(DEFAULT_DESCRIPTION_HTML);
    }

    public static DescriptionHtml emptyDescriptionHtml() {
        return DescriptionHtml.empty();
    }

    public static DescriptionHtml descriptionHtml(String value) {
        return DescriptionHtml.of(value);
    }

    public static ImageUrl defaultImageUrl() {
        return ImageUrl.of(DEFAULT_IMAGE_URL);
    }

    public static ImageUrl imageUrl(String value) {
        return ImageUrl.of(value);
    }

    public static CdnPath defaultCdnPath() {
        return CdnPath.of(DEFAULT_CDN_PATH);
    }

    public static OptionGroupName defaultOptionGroupName() {
        return OptionGroupName.of(DEFAULT_OPTION_GROUP_NAME);
    }

    public static OptionGroupName optionGroupName(String value) {
        return OptionGroupName.of(value);
    }

    public static OptionValueName defaultOptionValueName() {
        return OptionValueName.of(DEFAULT_OPTION_VALUE_NAME);
    }

    public static OptionValueName optionValueName(String value) {
        return OptionValueName.of(value);
    }

    // ===== Entity Fixtures =====

    /** 기본 ProductGroupImage (THUMBNAIL) */
    public static ProductGroupImage defaultProductGroupImage() {
        return ProductGroupImage.forNew(
                newProductGroupId(), defaultImageUrl(), ImageType.THUMBNAIL, 0);
    }

    /** 썸네일 이미지 */
    public static ProductGroupImage thumbnailImage() {
        return ProductGroupImage.forNew(
                newProductGroupId(), defaultImageUrl(), ImageType.THUMBNAIL, 0);
    }

    /** 상세 이미지 */
    public static ProductGroupImage detailImage(int sortOrder) {
        return ProductGroupImage.forNew(
                newProductGroupId(),
                imageUrl("https://example.com/detail" + sortOrder + ".jpg"),
                ImageType.DETAIL,
                sortOrder);
    }

    /** 업로드 완료된 이미지 */
    public static ProductGroupImage uploadedImage() {
        ProductGroupImage image = thumbnailImage();
        image.updateUploadedUrl(imageUrl("https://s3.example.com/uploaded.jpg"));
        return image;
    }

    /** 기본 DescriptionImage */
    public static DescriptionImage defaultDescriptionImage() {
        return DescriptionImage.forNew(defaultImageUrl(), 0);
    }

    /** 업로드 완료된 DescriptionImage */
    public static DescriptionImage uploadedDescriptionImage() {
        DescriptionImage image = defaultDescriptionImage();
        image.updateUploadedUrl(imageUrl("https://s3.example.com/desc-uploaded.jpg"));
        return image;
    }

    /** 기본 SellerOptionValue */
    public static SellerOptionValue defaultSellerOptionValue() {
        return SellerOptionValue.forNew(SellerOptionGroupId.forNew(), defaultOptionValueName(), 0);
    }

    /** 캐노니컬 매핑된 SellerOptionValue */
    public static SellerOptionValue mappedSellerOptionValue() {
        return SellerOptionValue.forNewWithCanonical(
                SellerOptionGroupId.forNew(),
                defaultOptionValueName(),
                CanonicalOptionValueId.of(1L),
                0);
    }

    /** 기본 SellerOptionGroup */
    public static SellerOptionGroup defaultSellerOptionGroup() {
        return SellerOptionGroup.forNew(
                newProductGroupId(),
                defaultOptionGroupName(),
                0,
                List.of(defaultSellerOptionValue()));
    }

    /** 캐노니컬 매핑된 SellerOptionGroup */
    public static SellerOptionGroup mappedSellerOptionGroup() {
        return SellerOptionGroup.forNewWithCanonical(
                newProductGroupId(),
                defaultOptionGroupName(),
                CanonicalOptionGroupId.of(1L),
                0,
                List.of(mappedSellerOptionValue()));
    }

    /** 완전 매핑된 SellerOptionGroup (그룹과 모든 값이 매핑됨) */
    public static SellerOptionGroup fullyMappedSellerOptionGroup() {
        SellerOptionValue value1 =
                SellerOptionValue.forNewWithCanonical(
                        SellerOptionGroupId.forNew(),
                        optionValueName("검정"),
                        CanonicalOptionValueId.of(1L),
                        0);
        SellerOptionValue value2 =
                SellerOptionValue.forNewWithCanonical(
                        SellerOptionGroupId.forNew(),
                        optionValueName("흰색"),
                        CanonicalOptionValueId.of(2L),
                        1);

        return SellerOptionGroup.forNewWithCanonical(
                newProductGroupId(),
                optionGroupName("색상"),
                CanonicalOptionGroupId.of(1L),
                0,
                List.of(value1, value2));
    }

    /** 기본 ProductGroupDescription */
    public static ProductGroupDescription defaultProductGroupDescription() {
        return ProductGroupDescription.forNew(newProductGroupId(), defaultDescriptionHtml());
    }

    /** 이미지 포함 ProductGroupDescription */
    public static ProductGroupDescription descriptionWithImages() {
        return ProductGroupDescription.reconstitute(
                defaultProductGroupDescriptionId(),
                newProductGroupId(),
                defaultDescriptionHtml(),
                defaultCdnPath(),
                List.of(defaultDescriptionImage(), uploadedDescriptionImage()));
    }

    // ===== ProductGroup Aggregate Fixtures =====

    /** 기본 ProductGroupImages (THUMBNAIL 1개) */
    public static ProductGroupImages defaultProductGroupImages() {
        return ProductGroupImages.of(List.of(thumbnailImage()));
    }

    /** THUMBNAIL + DETAIL 이미지 포함 ProductGroupImages */
    public static ProductGroupImages productGroupImagesWithDetails() {
        return ProductGroupImages.of(List.of(thumbnailImage(), detailImage(1), detailImage(2)));
    }

    /** 신규 ProductGroup (DRAFT 상태, 옵션 없음) */
    public static ProductGroup newProductGroup() {
        return ProductGroup.forNew(
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.NONE,
                defaultProductGroupImages(),
                SellerOptionGroups.of(List.of()),
                CommonVoFixtures.now());
    }

    /** 단일 옵션 신규 ProductGroup */
    public static ProductGroup newProductGroupWithSingleOption() {
        return ProductGroup.forNew(
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.SINGLE,
                defaultProductGroupImages(),
                SellerOptionGroups.of(List.of(defaultSellerOptionGroup())),
                CommonVoFixtures.now());
    }

    /** 조합 옵션 신규 ProductGroup */
    public static ProductGroup newProductGroupWithCombinationOption() {
        SellerOptionGroup colorOption =
                SellerOptionGroup.forNew(
                        newProductGroupId(),
                        optionGroupName("색상"),
                        0,
                        List.of(defaultSellerOptionValue()));

        SellerOptionGroup sizeOption =
                SellerOptionGroup.forNew(
                        newProductGroupId(),
                        optionGroupName("사이즈"),
                        1,
                        List.of(
                                SellerOptionValue.forNew(
                                        SellerOptionGroupId.forNew(), optionValueName("L"), 0)));

        return ProductGroup.forNew(
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.COMBINATION,
                defaultProductGroupImages(),
                SellerOptionGroups.of(List.of(colorOption, sizeOption)),
                CommonVoFixtures.now());
    }

    /** ACTIVE 상태의 ProductGroup */
    public static ProductGroup activeProductGroup() {
        return ProductGroup.reconstitute(
                defaultProductGroupId(),
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.NONE,
                ProductGroupStatus.ACTIVE,
                List.of(uploadedImage()),
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** DRAFT 상태의 ProductGroup */
    public static ProductGroup draftProductGroup(Long id) {
        return ProductGroup.reconstitute(
                ProductGroupId.of(id),
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.NONE,
                ProductGroupStatus.DRAFT,
                List.of(thumbnailImage()),
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** INACTIVE 상태의 ProductGroup */
    public static ProductGroup inactiveProductGroup() {
        return ProductGroup.reconstitute(
                defaultProductGroupId(),
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.NONE,
                ProductGroupStatus.INACTIVE,
                List.of(uploadedImage()),
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** SOLDOUT 상태의 ProductGroup */
    public static ProductGroup soldoutProductGroup() {
        return ProductGroup.reconstitute(
                defaultProductGroupId(),
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.NONE,
                ProductGroupStatus.SOLDOUT,
                List.of(uploadedImage()),
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** DELETED 상태의 ProductGroup */
    public static ProductGroup deletedProductGroup() {
        return ProductGroup.reconstitute(
                defaultProductGroupId(),
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.NONE,
                ProductGroupStatus.DELETED,
                List.of(uploadedImage()),
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** 썸네일 없는 ProductGroup */
    public static ProductGroup productGroupWithoutThumbnail() {
        return ProductGroup.reconstitute(
                defaultProductGroupId(),
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.NONE,
                ProductGroupStatus.DRAFT,
                List.of(detailImage(0)),
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** 완전 매핑된 ProductGroup */
    public static ProductGroup fullyMappedProductGroup() {
        return ProductGroup.reconstitute(
                defaultProductGroupId(),
                SellerId.of(DEFAULT_SELLER_ID),
                BrandId.of(DEFAULT_BRAND_ID),
                CategoryId.of(DEFAULT_CATEGORY_ID),
                ShippingPolicyId.of(DEFAULT_SHIPPING_POLICY_ID),
                RefundPolicyId.of(DEFAULT_REFUND_POLICY_ID),
                defaultProductGroupName(),
                OptionType.SINGLE,
                ProductGroupStatus.DRAFT,
                List.of(thumbnailImage()),
                List.of(fullyMappedSellerOptionGroup()),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
