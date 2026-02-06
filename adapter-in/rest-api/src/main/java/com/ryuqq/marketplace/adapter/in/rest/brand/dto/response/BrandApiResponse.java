package com.ryuqq.marketplace.adapter.in.rest.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 브랜드 조회 API 응답 DTO. */
@Schema(description = "브랜드 조회 응답")
public record BrandApiResponse(
        @Schema(description = "브랜드 ID") Long id,
        @Schema(description = "브랜드 코드") String code,
        @Schema(description = "한글명") String nameKo,
        @Schema(description = "영문명") String nameEn,
        @Schema(description = "약칭") String shortName,
        @Schema(description = "상태") String status,
        @Schema(description = "로고 URL") String logoUrl,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt) {}
