package com.ryuqq.marketplace.adapter.in.rest.session.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

/**
 * 업로드 세션 완료 처리 API 요청 DTO.
 *
 * @param fileSize 업로드된 파일 크기 (바이트)
 * @param etag S3 ETag (nullable - CORS 제한으로 클라이언트가 못 받을 수 있음)
 */
@Schema(description = "업로드 세션 완료 처리 요청")
public record CompleteUploadSessionApiRequest(
        @Schema(description = "파일 크기 (바이트)", example = "1048576") @Positive long fileSize,
        @Schema(
                        description = "S3 ETag (nullable)",
                        example = "\"d41d8cd98f00b204e9800998ecf8427e\"",
                        nullable = true)
                String etag) {}
