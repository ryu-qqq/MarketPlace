package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 상세설명 이미지 API 응답 DTO. */
@Schema(description = "상세설명 이미지 응답")
public record DescriptionImageApiResponse(
        @Schema(description = "이미지 ID", example = "1") Long id,
        @Schema(description = "원본 URL", example = "https://example.com/desc-image.jpg")
                String originUrl,
        @Schema(description = "업로드 URL", example = "https://cdn.example.com/desc-image.jpg")
                String uploadedUrl,
        @Schema(description = "정렬 순서", example = "0") int sortOrder) {}
