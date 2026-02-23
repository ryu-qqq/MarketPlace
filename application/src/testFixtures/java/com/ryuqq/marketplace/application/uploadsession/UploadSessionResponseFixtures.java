package com.ryuqq.marketplace.application.uploadsession;

import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import java.time.Instant;

/**
 * UploadSession Application Response 테스트 Fixtures.
 *
 * <p>UploadSession 관련 응답 DTO 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class UploadSessionResponseFixtures {

    private UploadSessionResponseFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_SESSION_ID = "session-abc-12345";
    public static final String DEFAULT_PRESIGNED_URL =
            "https://s3.ap-northeast-2.amazonaws.com/bucket/products/images/test-image.jpg"
                    + "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Expires=900";
    public static final String DEFAULT_FILE_KEY = "products/images/test-image.jpg";
    public static final Instant DEFAULT_EXPIRES_AT = Instant.parse("2026-02-20T01:00:00Z");
    public static final String DEFAULT_ACCESS_URL =
            "https://cdn.example.com/products/images/test-image.jpg";

    // ===== PresignedUrlResponse =====

    public static PresignedUrlResponse presignedUrlResponse() {
        return new PresignedUrlResponse(
                DEFAULT_SESSION_ID,
                DEFAULT_PRESIGNED_URL,
                DEFAULT_FILE_KEY,
                DEFAULT_EXPIRES_AT,
                DEFAULT_ACCESS_URL);
    }

    public static PresignedUrlResponse presignedUrlResponse(String sessionId) {
        return new PresignedUrlResponse(
                sessionId,
                DEFAULT_PRESIGNED_URL,
                DEFAULT_FILE_KEY,
                DEFAULT_EXPIRES_AT,
                DEFAULT_ACCESS_URL);
    }

    public static PresignedUrlResponse presignedUrlResponse(
            String sessionId,
            String presignedUrl,
            String fileKey,
            Instant expiresAt,
            String accessUrl) {
        return new PresignedUrlResponse(sessionId, presignedUrl, fileKey, expiresAt, accessUrl);
    }
}
