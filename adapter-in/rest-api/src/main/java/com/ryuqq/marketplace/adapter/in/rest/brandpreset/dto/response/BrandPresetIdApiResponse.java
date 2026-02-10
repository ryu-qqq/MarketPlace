package com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 브랜드 프리셋 등록 응답 DTO. */
@Schema(description = "브랜드 프리셋 등록 응답")
public record BrandPresetIdApiResponse(
        @Schema(description = "프리셋 ID", example = "1005") Long id,
        @Schema(description = "등록일", example = "2025-12-17") String createdAt) {

    public static BrandPresetIdApiResponse of(Long id, String createdAt) {
        return new BrandPresetIdApiResponse(id, createdAt);
    }
}
