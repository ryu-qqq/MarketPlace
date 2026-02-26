package com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 카테고리 프리셋 등록 응답 DTO. */
@Schema(description = "카테고리 프리셋 등록 응답")
public record CategoryPresetIdApiResponse(
        @Schema(description = "프리셋 ID", example = "1005") Long id,
        @Schema(description = "등록일", example = "2025-12-17") String createdAt) {

    public static CategoryPresetIdApiResponse of(Long id, String createdAt) {
        return new CategoryPresetIdApiResponse(id, createdAt);
    }
}
