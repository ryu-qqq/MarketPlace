package com.ryuqq.marketplace.adapter.in.rest.session.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 레거시 호환 Presigned URL 발급 요청.
 *
 * @param fileName 파일명
 * @param imagePath 이미지 경로 구분 (PRODUCT, DESCRIPTION, QNA, CONTENT, IMAGE_COMPONENT, BANNER)
 * @param fileSize 파일 크기 (bytes, 미입력 시 기본값 10MB)
 */
@Schema(description = "레거시 호환 Presigned URL 발급 요청")
public record LegacyImagePresignedApiRequest(
        @Schema(description = "파일명", example = "product-image.jpg") @NotBlank String fileName,
        @Schema(description = "이미지 경로 구분", example = "PRODUCT") @NotBlank String imagePath,
        @Schema(description = "파일 크기 (bytes)") Long fileSize) {}
