package com.ryuqq.marketplace.adapter.out.persistence.imagevariant;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity.ImageVariantJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ImageVariantJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ImageVariantJpaEntity 관련 객체들을 생성합니다.
 */
public final class ImageVariantJpaEntityFixtures {

    private ImageVariantJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_SOURCE_IMAGE_ID = 100L;
    public static final ImageSourceType DEFAULT_SOURCE_TYPE = ImageSourceType.PRODUCT_GROUP_IMAGE;
    public static final ImageVariantType DEFAULT_VARIANT_TYPE = ImageVariantType.SMALL_WEBP;
    public static final String DEFAULT_RESULT_ASSET_ID = "asset-abc-123";
    public static final String DEFAULT_VARIANT_URL =
            "https://cdn.example.com/images/variant_300x300.webp";
    public static final Integer DEFAULT_WIDTH = 300;
    public static final Integer DEFAULT_HEIGHT = 300;

    // ===== Entity Fixtures =====

    /** SMALL_WEBP 타입의 신규 Entity 생성 (ID null). */
    public static ImageVariantJpaEntity newSmallWebpEntity() {
        Instant now = Instant.now();
        return ImageVariantJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageVariantType.SMALL_WEBP,
                DEFAULT_RESULT_ASSET_ID,
                DEFAULT_VARIANT_URL,
                300,
                300,
                now);
    }

    /** MEDIUM_WEBP 타입의 신규 Entity 생성 (ID null). */
    public static ImageVariantJpaEntity newMediumWebpEntity() {
        Instant now = Instant.now();
        return ImageVariantJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageVariantType.MEDIUM_WEBP,
                DEFAULT_RESULT_ASSET_ID,
                "https://cdn.example.com/images/variant_600x600.webp",
                600,
                600,
                now);
    }

    /** LARGE_WEBP 타입의 신규 Entity 생성 (ID null). */
    public static ImageVariantJpaEntity newLargeWebpEntity() {
        Instant now = Instant.now();
        return ImageVariantJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageVariantType.LARGE_WEBP,
                DEFAULT_RESULT_ASSET_ID,
                "https://cdn.example.com/images/variant_1200x1200.webp",
                1200,
                1200,
                now);
    }

    /** ORIGINAL_WEBP 타입의 신규 Entity 생성 (ID null, 크기 null). */
    public static ImageVariantJpaEntity newOriginalWebpEntity() {
        Instant now = Instant.now();
        return ImageVariantJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageVariantType.ORIGINAL_WEBP,
                DEFAULT_RESULT_ASSET_ID,
                "https://cdn.example.com/images/variant_original.webp",
                null,
                null,
                now);
    }

    /** ID를 지정한 Entity 생성. */
    public static ImageVariantJpaEntity entityWithId(Long id) {
        Instant now = Instant.now();
        return ImageVariantJpaEntity.create(
                id,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_RESULT_ASSET_ID,
                DEFAULT_VARIANT_URL,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                now);
    }

    /** 소스 이미지 ID를 지정한 신규 Entity 생성 (ID null). */
    public static ImageVariantJpaEntity newEntityWithSourceImageId(Long sourceImageId) {
        Instant now = Instant.now();
        return ImageVariantJpaEntity.create(
                null,
                sourceImageId,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_RESULT_ASSET_ID,
                DEFAULT_VARIANT_URL,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                now);
    }

    /** DESCRIPTION_IMAGE 소스 타입의 신규 Entity 생성 (ID null). */
    public static ImageVariantJpaEntity newDescriptionImageEntity() {
        Instant now = Instant.now();
        return ImageVariantJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                ImageSourceType.DESCRIPTION_IMAGE,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_RESULT_ASSET_ID,
                DEFAULT_VARIANT_URL,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                now);
    }

    /** 커스텀 소스 이미지 ID와 타입을 지정한 신규 Entity 생성 (ID null). */
    public static ImageVariantJpaEntity newEntityWith(
            Long sourceImageId, ImageSourceType sourceType, ImageVariantType variantType) {
        Instant now = Instant.now();
        return ImageVariantJpaEntity.create(
                null,
                sourceImageId,
                sourceType,
                variantType,
                DEFAULT_RESULT_ASSET_ID,
                DEFAULT_VARIANT_URL,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                now);
    }
}
