package com.ryuqq.marketplace.adapter.in.rest.imageupload;

import com.ryuqq.marketplace.adapter.in.rest.imageupload.dto.request.ImageUploadCallbackApiRequest;

/**
 * ImageUpload API 테스트 Fixtures.
 *
 * <p>ImageUpload REST API 테스트에서 사용하는 요청 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ImageUploadApiFixtures {

    private ImageUploadApiFixtures() {}

    // ===== 상수 =====

    public static final String DEFAULT_DOWNLOAD_TASK_ID = "DOWNLOAD-TASK-001";
    public static final String DEFAULT_ASSET_ID = "ASSET-001";
    public static final String DEFAULT_SOURCE_URL = "https://example.com/source-image.jpg";
    public static final String DEFAULT_S3_KEY = "uploads/2026/03/27/image-001.jpg";
    public static final String DEFAULT_BUCKET = "marketplace-images";
    public static final String DEFAULT_FILE_NAME = "image-001.jpg";
    public static final String DEFAULT_CONTENT_TYPE = "image/jpeg";
    public static final long DEFAULT_FILE_SIZE = 204800L;

    // ===== ImageUploadCallbackApiRequest - COMPLETED =====

    public static ImageUploadCallbackApiRequest completedCallbackRequest() {
        return new ImageUploadCallbackApiRequest(
                DEFAULT_DOWNLOAD_TASK_ID,
                DEFAULT_ASSET_ID,
                "COMPLETED",
                DEFAULT_SOURCE_URL,
                DEFAULT_S3_KEY,
                DEFAULT_BUCKET,
                DEFAULT_FILE_NAME,
                DEFAULT_CONTENT_TYPE,
                DEFAULT_FILE_SIZE,
                null);
    }

    public static ImageUploadCallbackApiRequest failedCallbackRequest() {
        return new ImageUploadCallbackApiRequest(
                DEFAULT_DOWNLOAD_TASK_ID,
                null,
                "FAILED",
                DEFAULT_SOURCE_URL,
                null,
                null,
                null,
                null,
                0L,
                "다운로드 처리 중 오류가 발생했습니다.");
    }

    public static ImageUploadCallbackApiRequest completedCallbackRequest(
            String downloadTaskId, String assetId, String s3Key) {
        return new ImageUploadCallbackApiRequest(
                downloadTaskId,
                assetId,
                "COMPLETED",
                DEFAULT_SOURCE_URL,
                s3Key,
                DEFAULT_BUCKET,
                DEFAULT_FILE_NAME,
                DEFAULT_CONTENT_TYPE,
                DEFAULT_FILE_SIZE,
                null);
    }
}
