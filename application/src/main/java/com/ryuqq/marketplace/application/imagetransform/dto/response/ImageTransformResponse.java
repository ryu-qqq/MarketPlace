package com.ryuqq.marketplace.application.imagetransform.dto.response;

/**
 * 이미지 변환 응답 DTO.
 *
 * @param transformRequestId FileFlow 변환 요청 ID
 * @param status 변환 상태 (PENDING, PROCESSING, COMPLETED, FAILED)
 * @param resultAssetId 변환 완료 시 결과 에셋 ID (미완료 시 null)
 * @param resultCdnUrl 변환 완료 시 CDN URL (미완료 시 null)
 * @param width 변환 결과 너비 (미완료 시 null)
 * @param height 변환 결과 높이 (미완료 시 null)
 */
public record ImageTransformResponse(
        String transformRequestId,
        String status,
        String resultAssetId,
        String resultCdnUrl,
        Integer width,
        Integer height) {

    public static ImageTransformResponse pending(String transformRequestId) {
        return new ImageTransformResponse(transformRequestId, "PENDING", null, null, null, null);
    }

    public static ImageTransformResponse processing(String transformRequestId) {
        return new ImageTransformResponse(transformRequestId, "PROCESSING", null, null, null, null);
    }

    public static ImageTransformResponse completed(
            String transformRequestId,
            String resultAssetId,
            String resultCdnUrl,
            Integer width,
            Integer height) {
        return new ImageTransformResponse(
                transformRequestId, "COMPLETED", resultAssetId, resultCdnUrl, width, height);
    }

    public static ImageTransformResponse failed(String transformRequestId) {
        return new ImageTransformResponse(transformRequestId, "FAILED", null, null, null, null);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isFailed() {
        return "FAILED".equals(status);
    }

    public boolean isTerminal() {
        return isCompleted() || isFailed();
    }
}
