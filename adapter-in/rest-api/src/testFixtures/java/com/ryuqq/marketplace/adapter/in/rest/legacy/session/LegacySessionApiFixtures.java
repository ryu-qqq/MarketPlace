package com.ryuqq.marketplace.adapter.in.rest.legacy.session;

import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.request.LegacyPresignedUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.response.LegacyPresignedUrlApiResponse;
import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.legacy.session.dto.response.LegacyPresignedUrlResult;

/**
 * Legacy Session API 테스트 Fixtures.
 *
 * <p>Legacy 이미지 업로드 세션 REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacySessionApiFixtures {

    private LegacySessionApiFixtures() {}

    // ===== 상수 =====
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

    public static LegacyPresignedUrlApiRequest requestWith(
            String fileName, String imagePath, Long fileSize) {
        return new LegacyPresignedUrlApiRequest(fileName, imagePath, fileSize);
    }

    // ===== Command Fixtures =====

    public static LegacyGetPresignedUrlCommand command() {
        return new LegacyGetPresignedUrlCommand(
                DEFAULT_FILE_NAME, DEFAULT_IMAGE_PATH, DEFAULT_FILE_SIZE);
    }

    public static LegacyGetPresignedUrlCommand commandWithoutFileSize() {
        return new LegacyGetPresignedUrlCommand(DEFAULT_FILE_NAME, DEFAULT_IMAGE_PATH, null);
    }

    // ===== Result Fixtures =====

    public static LegacyPresignedUrlResult result() {
        return new LegacyPresignedUrlResult(
                DEFAULT_SESSION_ID, DEFAULT_PRESIGNED_URL, DEFAULT_OBJECT_KEY);
    }

    public static LegacyPresignedUrlResult resultWith(
            String sessionId, String preSignedUrl, String objectKey) {
        return new LegacyPresignedUrlResult(sessionId, preSignedUrl, objectKey);
    }

    // ===== Response Fixtures =====

    public static LegacyPresignedUrlApiResponse apiResponse() {
        return new LegacyPresignedUrlApiResponse(
                DEFAULT_SESSION_ID, DEFAULT_PRESIGNED_URL, DEFAULT_OBJECT_KEY);
    }
}
