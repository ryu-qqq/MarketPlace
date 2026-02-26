package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 브랜드 매핑 ID 응답 DTO. */
@Schema(description = "외부 브랜드 매핑 ID 응답")
public record InboundBrandMappingIdApiResponse(
        @Schema(description = "외부 브랜드 매핑 ID", example = "1") Long id) {

    public static InboundBrandMappingIdApiResponse of(Long id) {
        return new InboundBrandMappingIdApiResponse(id);
    }
}
