package com.ryuqq.marketplace.adapter.in.rest.session;

import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.CompleteUploadSessionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.GenerateUploadUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.response.GenerateUploadUrlApiResponse;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.uploadsession.dto.command.CompleteUploadSessionCommand;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import java.time.Instant;

/**
 * UploadSession API 테스트 Fixtures.
 *
 * <p>UploadSession REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class UploadSessionApiFixtures {

    private UploadSessionApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_SESSION_ID = "sess-abc123-def456";
    public static final String DEFAULT_DIRECTORY = "product-images";
    public static final String DEFAULT_FILENAME = "image.jpg";
    public static final String DEFAULT_CONTENT_TYPE = "image/jpeg";
    public static final long DEFAULT_CONTENT_LENGTH = 1_048_576L;
    public static final String DEFAULT_PRESIGNED_URL =
            "https://s3.amazonaws.com/bucket/product-images/image.jpg?X-Amz-Signature=abc123";
    public static final String DEFAULT_FILE_KEY = "product-images/2025/02/image.jpg";
    public static final Instant DEFAULT_EXPIRES_AT = Instant.parse("2025-02-20T10:15:00Z");
    public static final String DEFAULT_ACCESS_URL =
            "https://cdn.example.com/product-images/2025/02/image.jpg";
    public static final long DEFAULT_FILE_SIZE = 1_048_576L;
    public static final String DEFAULT_ETAG = "\"d41d8cd98f00b204e9800998ecf8427e\"";

    // ===== GenerateUploadUrlApiRequest =====

    public static GenerateUploadUrlApiRequest generateUploadUrlRequest() {
        return new GenerateUploadUrlApiRequest(
                DEFAULT_DIRECTORY, DEFAULT_FILENAME, DEFAULT_CONTENT_TYPE, DEFAULT_CONTENT_LENGTH);
    }

    public static GenerateUploadUrlApiRequest generateUploadUrlRequest(
            String directory, String filename, String contentType, long contentLength) {
        return new GenerateUploadUrlApiRequest(directory, filename, contentType, contentLength);
    }

    // ===== CompleteUploadSessionApiRequest =====

    public static CompleteUploadSessionApiRequest completeUploadSessionRequest() {
        return new CompleteUploadSessionApiRequest(DEFAULT_FILE_SIZE, DEFAULT_ETAG);
    }

    public static CompleteUploadSessionApiRequest completeUploadSessionRequestWithoutEtag() {
        return new CompleteUploadSessionApiRequest(DEFAULT_FILE_SIZE, null);
    }

    // ===== PresignedUploadUrlRequest (Application) =====

    public static PresignedUploadUrlRequest presignedUploadUrlRequest() {
        return PresignedUploadUrlRequest.of(
                UploadDirectory.PRODUCT_IMAGES,
                DEFAULT_FILENAME,
                DEFAULT_CONTENT_TYPE,
                DEFAULT_CONTENT_LENGTH);
    }

    // ===== PresignedUrlResponse (Application) =====

    public static PresignedUrlResponse presignedUrlResponse() {
        return new PresignedUrlResponse(
                DEFAULT_SESSION_ID,
                DEFAULT_PRESIGNED_URL,
                DEFAULT_FILE_KEY,
                DEFAULT_EXPIRES_AT,
                DEFAULT_ACCESS_URL);
    }

    // ===== CompleteUploadSessionCommand (Application) =====

    public static CompleteUploadSessionCommand completeUploadSessionCommand() {
        return new CompleteUploadSessionCommand(
                DEFAULT_SESSION_ID, DEFAULT_FILE_SIZE, DEFAULT_ETAG);
    }

    public static CompleteUploadSessionCommand completeUploadSessionCommand(String sessionId) {
        return new CompleteUploadSessionCommand(sessionId, DEFAULT_FILE_SIZE, DEFAULT_ETAG);
    }

    // ===== GenerateUploadUrlApiResponse =====

    public static GenerateUploadUrlApiResponse generateUploadUrlApiResponse() {
        return new GenerateUploadUrlApiResponse(
                DEFAULT_SESSION_ID,
                DEFAULT_PRESIGNED_URL,
                DEFAULT_FILE_KEY,
                DEFAULT_EXPIRES_AT,
                DEFAULT_ACCESS_URL);
    }
}
