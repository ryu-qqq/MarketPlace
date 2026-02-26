package com.ryuqq.marketplace.application.imagetransform;

import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;

/**
 * ImageTransform Application Response 테스트 Fixtures.
 *
 * <p>ImageTransform 응답 DTO 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ImageTransformResponseFixtures {

    private ImageTransformResponseFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_TRANSFORM_REQUEST_ID = "tr-req-abc-123";
    public static final String DEFAULT_RESULT_ASSET_ID = "asset-result-456";
    public static final String DEFAULT_RESULT_CDN_URL =
            "https://cdn.example.com/variants/300x300.webp";
    public static final Integer DEFAULT_WIDTH = 300;
    public static final Integer DEFAULT_HEIGHT = 300;

    // ===== ImageTransformResponse Fixtures =====

    public static ImageTransformResponse pendingResponse() {
        return ImageTransformResponse.pending(DEFAULT_TRANSFORM_REQUEST_ID);
    }

    public static ImageTransformResponse processingResponse() {
        return ImageTransformResponse.processing(DEFAULT_TRANSFORM_REQUEST_ID);
    }

    public static ImageTransformResponse completedResponse() {
        return ImageTransformResponse.completed(
                DEFAULT_TRANSFORM_REQUEST_ID,
                DEFAULT_RESULT_ASSET_ID,
                DEFAULT_RESULT_CDN_URL,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT);
    }

    public static ImageTransformResponse completedResponse(
            String transformRequestId, String resultCdnUrl) {
        return ImageTransformResponse.completed(
                transformRequestId,
                DEFAULT_RESULT_ASSET_ID,
                resultCdnUrl,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT);
    }

    public static ImageTransformResponse failedResponse() {
        return ImageTransformResponse.failed(DEFAULT_TRANSFORM_REQUEST_ID);
    }

    public static ImageTransformResponse failedResponse(String transformRequestId) {
        return ImageTransformResponse.failed(transformRequestId);
    }
}
