package com.ryuqq.marketplace.adapter.in.rest.imagetransform;

/**
 * ImageTransformPublicEndpoints - 이미지 변환 공개 API 엔드포인트 상수.
 *
 * <p>FileFlow 콜백용 공개 엔드포인트. 인증 없이 접근 가능.
 *
 * <p>API-END-001: Endpoints final class + private 생성자.
 *
 * <p>API-END-002: static final 상수.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ImageTransformPublicEndpoints {

    private ImageTransformPublicEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 기본 경로 */
    public static final String BASE = "/api/v1/market/public/image-transform";

    /** FileFlow 변환 완료 콜백 경로 */
    public static final String CALLBACK = BASE + "/callback";
}
