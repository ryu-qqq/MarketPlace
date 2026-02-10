package com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 카테고리 매핑 ID 응답 DTO. */
@Schema(description = "카테고리 매핑 ID 응답")
public record CategoryMappingIdApiResponse(
        @Schema(description = "카테고리 매핑 ID", example = "1") Long categoryMappingId) {

    public static CategoryMappingIdApiResponse of(Long categoryMappingId) {
        return new CategoryMappingIdApiResponse(categoryMappingId);
    }
}
