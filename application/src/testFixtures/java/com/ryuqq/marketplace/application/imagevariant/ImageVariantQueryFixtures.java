package com.ryuqq.marketplace.application.imagevariant;

import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.util.List;

/**
 * ImageVariant Application Query 테스트 Fixtures.
 *
 * <p>ImageVariant 조회 관련 DTO 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ImageVariantQueryFixtures {

    private ImageVariantQueryFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_SOURCE_IMAGE_ID = 100L;
    public static final String DEFAULT_RESULT_ASSET_ID = "test-asset-001";
    public static final String DEFAULT_VARIANT_URL =
            "https://cdn.example.com/images/variant_300x300.webp";
    public static final int DEFAULT_WIDTH = 300;
    public static final int DEFAULT_HEIGHT = 300;

    // ===== ImageVariantResult Fixtures =====

    public static ImageVariantResult imageVariantResult() {
        return new ImageVariantResult(
                ImageVariantType.SMALL_WEBP, DEFAULT_RESULT_ASSET_ID, DEFAULT_VARIANT_URL, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static ImageVariantResult imageVariantResult(ImageVariantType variantType) {
        return new ImageVariantResult(
                variantType, DEFAULT_RESULT_ASSET_ID, DEFAULT_VARIANT_URL, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static ImageVariantResult imageVariantResult(
            String variantUrl, Integer width, Integer height) {
        return new ImageVariantResult(ImageVariantType.SMALL_WEBP, DEFAULT_RESULT_ASSET_ID, variantUrl, width, height);
    }

    public static ImageVariantResult mediumWebpResult() {
        return new ImageVariantResult(
                ImageVariantType.MEDIUM_WEBP,
                DEFAULT_RESULT_ASSET_ID,
                "https://cdn.example.com/images/variant_600x600.webp",
                600,
                600);
    }

    public static ImageVariantResult originalWebpResult() {
        return new ImageVariantResult(
                ImageVariantType.ORIGINAL_WEBP,
                DEFAULT_RESULT_ASSET_ID,
                "https://cdn.example.com/images/variant_original.webp",
                null,
                null);
    }

    public static List<ImageVariantResult> imageVariantResults() {
        return List.of(imageVariantResult(), mediumWebpResult(), originalWebpResult());
    }

    public static List<ImageVariantResult> emptyResults() {
        return List.of();
    }
}
