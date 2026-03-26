package com.ryuqq.marketplace.adapter.in.rest.session.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 레거시 호환 Presigned URL 발급 응답.
 *
 * @param sessionId 업로드 세션 ID
 * @param preSignedUrl S3 Presigned URL
 * @param objectKey S3 객체 키
 */
@Schema(description = "레거시 호환 Presigned URL 발급 응답")
public record LegacyImagePresignedApiResponse(
        @Schema(description = "업로드 세션 ID") String sessionId,
        @Schema(description = "S3 Presigned URL") String preSignedUrl,
        @Schema(description = "S3 객체 키") String objectKey) {}
