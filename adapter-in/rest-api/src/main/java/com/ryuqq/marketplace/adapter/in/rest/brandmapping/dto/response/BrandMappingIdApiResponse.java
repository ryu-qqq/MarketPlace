package com.ryuqq.marketplace.adapter.in.rest.brandmapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 브랜드 매핑 ID 응답 DTO. */
@Schema(description = "브랜드 매핑 ID 응답")
public record BrandMappingIdApiResponse(
        @Schema(description = "브랜드 매핑 ID", example = "1") Long brandMappingId) {

    public static BrandMappingIdApiResponse of(Long brandMappingId) {
        return new BrandMappingIdApiResponse(brandMappingId);
    }
}
