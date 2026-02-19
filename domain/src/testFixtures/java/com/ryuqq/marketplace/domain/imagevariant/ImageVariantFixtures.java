package com.ryuqq.marketplace.domain.imagevariant;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariant.id.ImageVariantId;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageDimension;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ResultAssetId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;

/**
 * ImageVariant 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ImageVariant 관련 객체들을 생성합니다.
 */
public final class ImageVariantFixtures {

    private ImageVariantFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_SOURCE_IMAGE_ID = 100L;
    public static final ImageSourceType DEFAULT_SOURCE_TYPE = ImageSourceType.PRODUCT_GROUP_IMAGE;
    public static final String DEFAULT_VARIANT_URL =
            "https://cdn.example.com/images/variant_300x300.webp";
    public static final String DEFAULT_RESULT_ASSET_ID = "asset-abc-123";

    // ===== ImageVariant Aggregate Fixtures =====

    /** SMALL_WEBP 타입의 신규 ImageVariant 생성. */
    public static ImageVariant newSmallWebpVariant() {
        return ImageVariant.forNew(
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageVariantType.SMALL_WEBP,
                ResultAssetId.of(DEFAULT_RESULT_ASSET_ID),
                ImageUrl.of(DEFAULT_VARIANT_URL),
                ImageDimension.of(300, 300),
                CommonVoFixtures.now());
    }

    /** MEDIUM_WEBP 타입의 신규 ImageVariant 생성. */
    public static ImageVariant newMediumWebpVariant() {
        return ImageVariant.forNew(
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageVariantType.MEDIUM_WEBP,
                ResultAssetId.of(DEFAULT_RESULT_ASSET_ID),
                ImageUrl.of("https://cdn.example.com/images/variant_600x600.webp"),
                ImageDimension.of(600, 600),
                CommonVoFixtures.now());
    }

    /** ORIGINAL_WEBP 타입의 신규 ImageVariant 생성. */
    public static ImageVariant newOriginalWebpVariant() {
        return ImageVariant.forNew(
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageVariantType.ORIGINAL_WEBP,
                ResultAssetId.of(DEFAULT_RESULT_ASSET_ID),
                ImageUrl.of("https://cdn.example.com/images/variant_original.webp"),
                ImageDimension.of(null, null),
                CommonVoFixtures.now());
    }

    /** reconstituted ImageVariant (ID 지정). */
    public static ImageVariant reconstitutedVariant(Long id) {
        return ImageVariant.reconstitute(
                ImageVariantId.of(id),
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageVariantType.SMALL_WEBP,
                ResultAssetId.of(DEFAULT_RESULT_ASSET_ID),
                ImageUrl.of(DEFAULT_VARIANT_URL),
                ImageDimension.of(300, 300),
                CommonVoFixtures.yesterday());
    }

    // ===== VO Fixtures =====

    public static ImageVariantId variantId(Long value) {
        return ImageVariantId.of(value);
    }

    public static ImageVariantId newVariantId() {
        return ImageVariantId.forNew();
    }

    public static ResultAssetId resultAssetId() {
        return ResultAssetId.of(DEFAULT_RESULT_ASSET_ID);
    }

    public static ResultAssetId resultAssetId(String value) {
        return ResultAssetId.of(value);
    }

    public static ImageDimension dimension(Integer width, Integer height) {
        return ImageDimension.of(width, height);
    }
}
