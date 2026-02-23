package com.ryuqq.marketplace.adapter.in.rest.session.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Presigned URL 발급 API 응답 DTO.
 *
 * @param sessionId 업로드 세션 ID
 * @param presignedUrl Presigned URL
 * @param fileKey 파일 저장 경로 (S3 Object Key)
 * @param expiresAt URL 만료 시간
 * @param accessUrl CDN 접근 URL
 */
@Schema(description = "Presigned URL 발급 응답")
public record GenerateUploadUrlApiResponse(
        @Schema(description = "업로드 세션 ID", example = "sess-abc123") String sessionId,
        @Schema(description = "Presigned URL", example = "https://s3.amazonaws.com/...")
                String presignedUrl,
        @Schema(description = "파일 저장 경로", example = "product-images/image.jpg") String fileKey,
        @Schema(description = "URL 만료 시간") Instant expiresAt,
        @Schema(
                        description = "CDN 접근 URL",
                        example = "https://cdn.example.com/product-images/image.jpg")
                String accessUrl) {}
