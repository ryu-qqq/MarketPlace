package com.ryuqq.marketplace.application.imagetransform.dto.command;

/**
 * 이미지 변환 콜백 완료 커맨드.
 *
 * <p>FileFlow에서 변환 완료 시 콜백으로 전달되는 데이터입니다.
 *
 * @param transformRequestId FileFlow 변환 요청 ID
 * @param status 변환 상태 (COMPLETED, FAILED)
 * @param resultAssetId 변환 결과 에셋 ID (COMPLETED 시)
 * @param resultCdnUrl 변환 결과 CDN URL (COMPLETED 시)
 * @param width 변환 결과 너비 (COMPLETED 시)
 * @param height 변환 결과 높이 (COMPLETED 시)
 * @param lastError 에러 메시지 (FAILED 시)
 */
public record CompleteImageTransformCallbackCommand(
        String transformRequestId,
        String status,
        String resultAssetId,
        String resultCdnUrl,
        Integer width,
        Integer height,
        String lastError) {}
