package com.ryuqq.marketplace.application.imagetransform.port.out.client;

import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;

/**
 * 이미지 변환 클라이언트 인터페이스.
 *
 * <p>FileFlow SDK TransformRequestApi를 추상화한 포트입니다.
 */
public interface ImageTransformClient {

    /**
     * 이미지 변환 요청을 생성합니다.
     *
     * @param uploadedUrl 업로드된 CDN URL
     * @param variantType 변환 대상 Variant 타입
     * @return 변환 요청 응답
     */
    ImageTransformResponse createTransformRequest(String uploadedUrl, ImageVariantType variantType);

    /**
     * 이미지 변환 요청 상태를 조회합니다.
     *
     * @param transformRequestId FileFlow 변환 요청 ID
     * @return 변환 요청 응답
     */
    ImageTransformResponse getTransformRequest(String transformRequestId);
}
