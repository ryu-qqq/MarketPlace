package com.ryuqq.marketplace.application.uploadsession;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.uploadsession.dto.command.CompleteUploadSessionCommand;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;

/**
 * UploadSession Application Command 테스트 Fixtures.
 *
 * <p>UploadSession 관련 Command 및 Request 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class UploadSessionCommandFixtures {

    private UploadSessionCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final UploadDirectory DEFAULT_DIRECTORY = UploadDirectory.PRODUCT_IMAGES;
    public static final String DEFAULT_FILENAME = "test-image.jpg";
    public static final String DEFAULT_CONTENT_TYPE = "image/jpeg";
    public static final long DEFAULT_CONTENT_LENGTH = 204800L;

    public static final String DEFAULT_SESSION_ID = "session-abc-12345";
    public static final long DEFAULT_FILE_SIZE = 204800L;
    public static final String DEFAULT_ETAG = "\"d41d8cd98f00b204e9800998ecf8427e\"";

    // ===== PresignedUploadUrlRequest =====

    public static PresignedUploadUrlRequest presignedUploadUrlRequest() {
        return PresignedUploadUrlRequest.of(
                DEFAULT_DIRECTORY, DEFAULT_FILENAME, DEFAULT_CONTENT_TYPE, DEFAULT_CONTENT_LENGTH);
    }

    public static PresignedUploadUrlRequest presignedUploadUrlRequest(
            UploadDirectory directory, String filename, String contentType, long contentLength) {
        return PresignedUploadUrlRequest.of(directory, filename, contentType, contentLength);
    }

    // ===== CompleteUploadSessionCommand =====

    public static CompleteUploadSessionCommand completeUploadSessionCommand() {
        return new CompleteUploadSessionCommand(
                DEFAULT_SESSION_ID, DEFAULT_FILE_SIZE, DEFAULT_ETAG);
    }

    public static CompleteUploadSessionCommand completeUploadSessionCommand(
            String sessionId, long fileSize, String etag) {
        return new CompleteUploadSessionCommand(sessionId, fileSize, etag);
    }

    public static CompleteUploadSessionCommand completeUploadSessionCommandWithoutEtag() {
        return new CompleteUploadSessionCommand(DEFAULT_SESSION_ID, DEFAULT_FILE_SIZE, null);
    }
}
