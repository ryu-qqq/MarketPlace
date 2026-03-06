package com.ryuqq.marketplace.adapter.in.rest.imagetransform.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * FileFlow 이미지 변환 콜백 요청 DTO.
 *
 * <p>FileFlow가 변환 완료 시 전송하는 콜백 본문.
 *
 * @param transformRequestId 변환 요청 ID
 * @param status 변환 상태 (COMPLETED, FAILED)
 * @param sourceAssetId 원본 에셋 ID
 * @param resultAssetId 결과 에셋 ID (COMPLETED 시)
 * @param transformType 변환 타입 (COMPLETED 시, e.g. IMAGE_RESIZE)
 * @param width 변환 결과 너비 (COMPLETED 시, nullable)
 * @param height 변환 결과 높이 (COMPLETED 시, nullable)
 * @param quality 변환 품질 (COMPLETED 시, nullable)
 * @param targetFormat 변환 포맷 (COMPLETED 시, nullable, e.g. webp)
 * @param errorMessage 에러 메시지 (FAILED 시)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImageTransformCallbackApiRequest(
        String transformRequestId,
        String status,
        String sourceAssetId,
        String resultAssetId,
        String transformType,
        Integer width,
        Integer height,
        Integer quality,
        String targetFormat,
        String errorMessage) {}
