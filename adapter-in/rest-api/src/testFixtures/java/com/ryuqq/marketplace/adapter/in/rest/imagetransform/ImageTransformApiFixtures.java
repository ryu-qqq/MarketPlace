package com.ryuqq.marketplace.adapter.in.rest.imagetransform;

import com.ryuqq.marketplace.adapter.in.rest.imagetransform.dto.request.ImageTransformCallbackApiRequest;

/**
 * ImageTransform API 테스트 Fixtures.
 *
 * <p>ImageTransform REST API 테스트에서 사용하는 요청 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ImageTransformApiFixtures {

    private ImageTransformApiFixtures() {}

    // ===== 상수 =====

    public static final String DEFAULT_TRANSFORM_REQUEST_ID = "TRANSFORM-REQ-001";
    public static final String DEFAULT_SOURCE_ASSET_ID = "ASSET-SRC-001";
    public static final String DEFAULT_RESULT_ASSET_ID = "ASSET-RESULT-001";
    public static final String DEFAULT_TRANSFORM_TYPE = "IMAGE_RESIZE";
    public static final String DEFAULT_TARGET_FORMAT = "webp";

    // ===== ImageTransformCallbackApiRequest - COMPLETED =====

    public static ImageTransformCallbackApiRequest completedCallbackRequest() {
        return new ImageTransformCallbackApiRequest(
                DEFAULT_TRANSFORM_REQUEST_ID,
                "COMPLETED",
                DEFAULT_SOURCE_ASSET_ID,
                DEFAULT_RESULT_ASSET_ID,
                DEFAULT_TRANSFORM_TYPE,
                800,
                600,
                85,
                DEFAULT_TARGET_FORMAT,
                null);
    }

    public static ImageTransformCallbackApiRequest failedCallbackRequest() {
        return new ImageTransformCallbackApiRequest(
                DEFAULT_TRANSFORM_REQUEST_ID,
                "FAILED",
                DEFAULT_SOURCE_ASSET_ID,
                null,
                null,
                null,
                null,
                null,
                null,
                "변환 처리 중 오류가 발생했습니다.");
    }

    public static ImageTransformCallbackApiRequest completedCallbackRequest(
            String transformRequestId, String resultAssetId, int width, int height) {
        return new ImageTransformCallbackApiRequest(
                transformRequestId,
                "COMPLETED",
                DEFAULT_SOURCE_ASSET_ID,
                resultAssetId,
                DEFAULT_TRANSFORM_TYPE,
                width,
                height,
                85,
                DEFAULT_TARGET_FORMAT,
                null);
    }
}
