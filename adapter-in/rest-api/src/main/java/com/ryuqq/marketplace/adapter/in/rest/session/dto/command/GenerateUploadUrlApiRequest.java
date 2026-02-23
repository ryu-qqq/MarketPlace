package com.ryuqq.marketplace.adapter.in.rest.session.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Presigned URL 발급 API 요청 DTO.
 *
 * @param directory 업로드 디렉토리/카테고리
 * @param filename 파일명
 * @param contentType MIME 타입
 * @param contentLength 파일 크기 (바이트)
 */
@Schema(description = "Presigned URL 발급 요청")
public record GenerateUploadUrlApiRequest(
        @Schema(description = "업로드 디렉토리", example = "product-images") @NotBlank String directory,
        @Schema(description = "파일명", example = "image.jpg") @NotBlank String filename,
        @Schema(description = "MIME 타입", example = "image/jpeg") @NotBlank String contentType,
        @Schema(description = "파일 크기 (바이트)", example = "1048576") @Positive long contentLength) {}
