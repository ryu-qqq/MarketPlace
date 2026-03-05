package com.ryuqq.marketplace.adapter.in.rest.imagetransform.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * FileFlow 이미지 변환 콜백 요청 DTO.
 *
 * <p>FileFlow가 변환 완료 시 전송하는 콜백 본문.
 *
 * @param transformRequestId 변환 요청 ID
 * @param status 변환 상태 (COMPLETED, FAILED)
 * @param resultAssetId 결과 에셋 ID (COMPLETED 시)
 * @param resultCdnUrl 결과 CDN URL (COMPLETED 시)
 * @param width 변환 결과 너비 (COMPLETED 시, nullable)
 * @param height 변환 결과 높이 (COMPLETED 시, nullable)
 * @param errorMessage 에러 메시지 (FAILED 시)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImageTransformCallbackApiRequest(
        String transformRequestId,
        String status,
        String resultAssetId,
        String resultCdnUrl,
        Integer width,
        Integer height,
        String errorMessage) {}
