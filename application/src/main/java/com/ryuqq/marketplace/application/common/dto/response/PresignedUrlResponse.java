package com.ryuqq.marketplace.application.common.dto.response;

import java.time.Instant;

/**
 * Presigned URL 발급 응답 DTO.
 *
 * @param sessionId 업로드 세션 ID
 * @param presignedUrl Presigned URL
 * @param fileKey 파일 저장 경로 (S3 Object Key)
 * @param expiresAt URL 만료 시간
 * @param accessUrl CDN 접근 URL
 */
public record PresignedUrlResponse(
        String sessionId,
        String presignedUrl,
        String fileKey,
        Instant expiresAt,
        String accessUrl) {}
