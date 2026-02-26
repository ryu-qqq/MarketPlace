package com.ryuqq.marketplace.adapter.in.rest.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 브랜드 조회 API 응답 DTO. */
@Schema(description = "브랜드 조회 응답")
public record BrandApiResponse(
        @Schema(description = "브랜드 ID", example = "1") Long id,
        @Schema(description = "브랜드 코드", example = "BR001") String code,
        @Schema(description = "한글명", example = "나이키") String nameKo,
        @Schema(description = "영문명", example = "NIKE") String nameEn,
        @Schema(description = "약칭", example = "NK") String shortName,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status,
        @Schema(description = "로고 URL", example = "https://cdn.example.com/logo.png")
                String logoUrl,
        @Schema(description = "생성일시 (ISO 8601)", example = "2026-01-15T09:30:00Z") String createdAt,
        @Schema(description = "수정일시 (ISO 8601)", example = "2026-02-10T14:20:00Z")
                String updatedAt) {}
