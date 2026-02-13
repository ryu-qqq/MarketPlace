package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 상품 그룹 이미지 API 응답 DTO. */
@Schema(description = "상품 그룹 이미지 응답")
public record ProductGroupImageApiResponse(
        @Schema(description = "이미지 ID") Long id,
        @Schema(description = "원본 URL") String originUrl,
        @Schema(description = "업로드 URL") String uploadedUrl,
        @Schema(description = "이미지 유형") String imageType,
        @Schema(description = "정렬 순서") int sortOrder) {}
