package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 브랜드 매핑 ID 응답 DTO. */
@Schema(description = "외부 브랜드 매핑 ID 응답")
public record ExternalBrandMappingIdApiResponse(
        @Schema(description = "외부 브랜드 매핑 ID", example = "1") Long id) {

    public static ExternalBrandMappingIdApiResponse of(Long id) {
        return new ExternalBrandMappingIdApiResponse(id);
    }
}
