package com.ryuqq.marketplace.application.common.dto.response;

/**
 * FileFlow 다운로드 태스크 상태 응답 DTO.
 *
 * @param downloadTaskId 다운로드 태스크 ID
 * @param status 태스크 상태 (PENDING, PROCESSING, COMPLETED, FAILED)
 * @param newCdnUrl 업로드된 CDN URL (COMPLETED 시)
 * @param fileAssetId FileFlow 에셋 ID (COMPLETED 시)
 * @param errorMessage 에러 메시지 (FAILED 시)
 */
public record ExternalDownloadStatusResponse(
        String downloadTaskId,
        String status,
        String newCdnUrl,
        String fileAssetId,
        String errorMessage) {

    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_PROCESSING = "PROCESSING";

    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    public boolean isFailed() {
        return STATUS_FAILED.equals(status);
    }

    public boolean isProcessing() {
        return STATUS_PROCESSING.equals(status) || "PENDING".equals(status);
    }

    public static ExternalDownloadStatusResponse completed(
            String downloadTaskId, String newCdnUrl, String fileAssetId) {
        return new ExternalDownloadStatusResponse(
                downloadTaskId, STATUS_COMPLETED, newCdnUrl, fileAssetId, null);
    }

    public static ExternalDownloadStatusResponse failed(
            String downloadTaskId, String errorMessage) {
        return new ExternalDownloadStatusResponse(
                downloadTaskId, STATUS_FAILED, null, null, errorMessage);
    }

    public static ExternalDownloadStatusResponse processing(String downloadTaskId) {
        return new ExternalDownloadStatusResponse(
                downloadTaskId, STATUS_PROCESSING, null, null, null);
    }
}
