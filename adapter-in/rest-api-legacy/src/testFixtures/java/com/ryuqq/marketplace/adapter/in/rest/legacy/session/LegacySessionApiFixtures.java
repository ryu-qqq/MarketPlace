package com.ryuqq.marketplace.adapter.in.rest.legacy.session;

import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.request.LegacyPresignedUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.response.LegacyPresignedUrlApiResponse;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import java.time.Instant;

/**
 * Legacy Session API 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacySessionApiFixtures {

    private LegacySessionApiFixtures() {}

    public static final String DEFAULT_FILE_NAME = "product-image.jpg";
    public static final String DEFAULT_IMAGE_PATH = "PRODUCT";
    public static final Long DEFAULT_FILE_SIZE = 1_048_576L;
    public static final String DEFAULT_SESSION_ID = "sess-abc123-def456";
    public static final String DEFAULT_PRESIGNED_URL =
            "https://s3.amazonaws.com/bucket/product-images/product-image.jpg?X-Amz-Signature=abc123";
    public static final String DEFAULT_OBJECT_KEY = "product-images/2025/02/product-image.jpg";

    // ===== Request Fixtures =====

    public static LegacyPresignedUrlApiRequest request() {
        return new LegacyPresignedUrlApiRequest(
                DEFAULT_FILE_NAME, DEFAULT_IMAGE_PATH, DEFAULT_FILE_SIZE);
    }

    public static LegacyPresignedUrlApiRequest requestWithoutFileSize() {
        return new LegacyPresignedUrlApiRequest(DEFAULT_FILE_NAME, DEFAULT_IMAGE_PATH, null);
    }

    // ===== Command Fixtures (표준) =====

    public static PresignedUploadUrlRequest command() {
        return PresignedUploadUrlRequest.of(
                UploadDirectory.PRODUCT_IMAGES, DEFAULT_FILE_NAME, "image/jpeg", DEFAULT_FILE_SIZE);
    }

    // ===== Result Fixtures (표준) =====

    public static PresignedUrlResponse presignedUrlResponse() {
        return new PresignedUrlResponse(
                DEFAULT_SESSION_ID,
                DEFAULT_PRESIGNED_URL,
                DEFAULT_OBJECT_KEY,
                Instant.now().plusSeconds(3600),
                "");
    }

    // ===== Response Fixtures =====

    public static LegacyPresignedUrlApiResponse apiResponse() {
        return new LegacyPresignedUrlApiResponse(
                DEFAULT_SESSION_ID, DEFAULT_PRESIGNED_URL, DEFAULT_OBJECT_KEY);
    }
}
