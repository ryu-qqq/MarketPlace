package com.ryuqq.marketplace.adapter.in.rest.externalmapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 매핑 통합 조회 응답 DTO. */
@Schema(description = "외부 매핑 통합 조회 응답")
public record ResolvedMappingApiResponse(
        @Schema(description = "내부 브랜드 ID") Long internalBrandId,
        @Schema(description = "내부 카테고리 ID") Long internalCategoryId,
        @Schema(description = "외부 소스 ID") Long externalSourceId) {}
