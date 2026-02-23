package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 카테고리 매핑 ID 응답 DTO. */
@Schema(description = "외부 카테고리 매핑 ID 응답")
public record InboundCategoryMappingIdApiResponse(
        @Schema(description = "외부 카테고리 매핑 ID", example = "1") Long id) {

    public static InboundCategoryMappingIdApiResponse of(Long id) {
        return new InboundCategoryMappingIdApiResponse(id);
    }
}
