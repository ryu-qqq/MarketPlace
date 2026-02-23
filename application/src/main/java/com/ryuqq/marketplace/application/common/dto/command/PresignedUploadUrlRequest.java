package com.ryuqq.marketplace.application.common.dto.command;

import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;

/**
 * Presigned Upload URL 요청.
 *
 * @param directory 업로드 디렉토리 (화이트리스트)
 * @param filename 파일명
 * @param contentType MIME 타입
 * @param contentLength 파일 크기 (바이트)
 */
public record PresignedUploadUrlRequest(
        UploadDirectory directory, String filename, String contentType, long contentLength) {

    public static PresignedUploadUrlRequest of(
            UploadDirectory directory, String filename, String contentType, long contentLength) {
        return new PresignedUploadUrlRequest(directory, filename, contentType, contentLength);
    }
}
