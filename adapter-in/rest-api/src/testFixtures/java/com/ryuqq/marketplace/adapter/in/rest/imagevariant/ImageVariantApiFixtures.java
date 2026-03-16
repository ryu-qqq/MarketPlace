package com.ryuqq.marketplace.adapter.in.rest.imagevariant;

import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.command.RequestImageTransformApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.response.ImageVariantApiResponse;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.util.List;

/**
 * ImageVariant API 테스트 Fixtures.
 *
 * <p>ImageVariant REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ImageVariantApiFixtures {

    private ImageVariantApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_IMAGE_ID = 200L;
    public static final String DEFAULT_RESULT_ASSET_ID = "test-asset-001";
    public static final String DEFAULT_VARIANT_URL =
            "https://cdn.example.com/images/small_300x300.webp";
    public static final String DEFAULT_SMALL_URL =
            "https://cdn.example.com/images/small_300x300.webp";
    public static final String DEFAULT_MEDIUM_URL =
            "https://cdn.example.com/images/medium_600x600.webp";
    public static final String DEFAULT_LARGE_URL =
            "https://cdn.example.com/images/large_1200x1200.webp";
    public static final String DEFAULT_ORIGINAL_URL =
            "https://cdn.example.com/images/original.webp";

    // ===== RequestImageTransformApiRequest =====

    public static RequestImageTransformApiRequest requestWithVariantTypes() {
        return new RequestImageTransformApiRequest(List.of("SMALL_WEBP", "MEDIUM_WEBP"));
    }

    public static RequestImageTransformApiRequest requestWithAllVariantTypes() {
        return new RequestImageTransformApiRequest(
                List.of("SMALL_WEBP", "MEDIUM_WEBP", "LARGE_WEBP", "ORIGINAL_WEBP"));
    }

    public static RequestImageTransformApiRequest requestWithNullVariantTypes() {
        return new RequestImageTransformApiRequest(null);
    }

    public static RequestImageTransformApiRequest requestWithEmptyVariantTypes() {
        return new RequestImageTransformApiRequest(List.of());
    }

    // ===== ImageVariantResult (Application) =====

    public static ImageVariantResult imageVariantResult(ImageVariantType type) {
        return switch (type) {
            case SMALL_WEBP -> new ImageVariantResult(type, DEFAULT_RESULT_ASSET_ID, DEFAULT_SMALL_URL, 300, 300);
            case MEDIUM_WEBP -> new ImageVariantResult(type, DEFAULT_RESULT_ASSET_ID, DEFAULT_MEDIUM_URL, 600, 600);
            case LARGE_WEBP -> new ImageVariantResult(type, DEFAULT_RESULT_ASSET_ID, DEFAULT_LARGE_URL, 1200, 1200);
            case ORIGINAL_WEBP -> new ImageVariantResult(type, DEFAULT_RESULT_ASSET_ID, DEFAULT_ORIGINAL_URL, null, null);
        };
    }

    public static List<ImageVariantResult> imageVariantResults() {
        return List.of(
                imageVariantResult(ImageVariantType.SMALL_WEBP),
                imageVariantResult(ImageVariantType.MEDIUM_WEBP),
                imageVariantResult(ImageVariantType.LARGE_WEBP),
                imageVariantResult(ImageVariantType.ORIGINAL_WEBP));
    }

    public static List<ImageVariantResult> imageVariantResults(ImageVariantType... types) {
        return java.util.Arrays.stream(types)
                .map(ImageVariantApiFixtures::imageVariantResult)
                .toList();
    }

    // ===== ImageVariantApiResponse =====

    public static ImageVariantApiResponse apiResponse(ImageVariantType type) {
        return switch (type) {
            case SMALL_WEBP ->
                    new ImageVariantApiResponse("SMALL_WEBP", DEFAULT_SMALL_URL, 300, 300);
            case MEDIUM_WEBP ->
                    new ImageVariantApiResponse("MEDIUM_WEBP", DEFAULT_MEDIUM_URL, 600, 600);
            case LARGE_WEBP ->
                    new ImageVariantApiResponse("LARGE_WEBP", DEFAULT_LARGE_URL, 1200, 1200);
            case ORIGINAL_WEBP ->
                    new ImageVariantApiResponse("ORIGINAL_WEBP", DEFAULT_ORIGINAL_URL, null, null);
        };
    }

    public static List<ImageVariantApiResponse> apiResponses() {
        return List.of(
                apiResponse(ImageVariantType.SMALL_WEBP),
                apiResponse(ImageVariantType.MEDIUM_WEBP),
                apiResponse(ImageVariantType.LARGE_WEBP),
                apiResponse(ImageVariantType.ORIGINAL_WEBP));
    }

    public static List<ImageVariantApiResponse> apiResponses(ImageVariantType... types) {
        return java.util.Arrays.stream(types).map(ImageVariantApiFixtures::apiResponse).toList();
    }
}
