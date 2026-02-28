package com.ryuqq.marketplace.application.imagetransform.dto.response;

/**
 * 이미지 변환 응답 DTO.
 *
 * @param transformRequestId FileFlow 변환 요청 ID
 * @param status 변환 상태
 * @param resultAssetId 변환 완료 시 결과 에셋 ID (미완료 시 null)
 * @param resultCdnUrl 변환 완료 시 CDN URL (미완료 시 null)
 * @param width 변환 결과 너비 (미완료 시 null)
 * @param height 변환 결과 높이 (미완료 시 null)
 */
public record ImageTransformResponse(
        String transformRequestId,
        ImageTransformStatus status,
        String resultAssetId,
        String resultCdnUrl,
        Integer width,
        Integer height) {

    public static ImageTransformResponse pending(String transformRequestId) {
        return new ImageTransformResponse(
                transformRequestId, ImageTransformStatus.PENDING, null, null, null, null);
    }

    public static ImageTransformResponse processing(String transformRequestId) {
        return new ImageTransformResponse(
                transformRequestId, ImageTransformStatus.PROCESSING, null, null, null, null);
    }

    public static ImageTransformResponse completed(
            String transformRequestId,
            String resultAssetId,
            String resultCdnUrl,
            Integer width,
            Integer height) {
        return new ImageTransformResponse(
                transformRequestId,
                ImageTransformStatus.COMPLETED,
                resultAssetId,
                resultCdnUrl,
                width,
                height);
    }

    public static ImageTransformResponse failed(String transformRequestId) {
        return new ImageTransformResponse(
                transformRequestId, ImageTransformStatus.FAILED, null, null, null, null);
    }

    public boolean isCompleted() {
        return status == ImageTransformStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == ImageTransformStatus.FAILED;
    }

    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }
}
