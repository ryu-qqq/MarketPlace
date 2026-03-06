package com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 레거시 Presigned URL 발급 응답.
 *
 * <p>세토프 어드민의 PreSignedUrlResponse와 호환됩니다.
 *
 * @param sessionId 업로드 세션 ID (업로드 완료 처리에 필요)
 * @param preSignedUrl S3 Presigned URL
 * @param objectKey S3 객체 키
 */
@Schema(description = "레거시 Presigned URL 발급 응답")
public record LegacyPresignedUrlApiResponse(
        @Schema(description = "업로드 세션 ID") String sessionId,
        @Schema(description = "S3 Presigned URL") String preSignedUrl,
        @Schema(description = "S3 객체 키") String objectKey) {}
