package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 카테고리 매핑 ID 응답 DTO. */
@Schema(description = "외부 카테고리 매핑 ID 응답")
public record ExternalCategoryMappingIdApiResponse(
        @Schema(description = "외부 카테고리 매핑 ID", example = "1") Long id) {

    public static ExternalCategoryMappingIdApiResponse of(Long id) {
        return new ExternalCategoryMappingIdApiResponse(id);
    }
}
