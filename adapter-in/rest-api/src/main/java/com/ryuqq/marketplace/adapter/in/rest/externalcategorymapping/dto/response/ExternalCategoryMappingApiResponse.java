package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 카테고리 매핑 조회 응답 DTO. */
@Schema(description = "외부 카테고리 매핑 조회 응답")
public record ExternalCategoryMappingApiResponse(
        @Schema(description = "매핑 ID") Long id,
        @Schema(description = "외부 소스 ID") Long externalSourceId,
        @Schema(description = "외부 카테고리 코드") String externalCategoryCode,
        @Schema(description = "외부 카테고리명") String externalCategoryName,
        @Schema(description = "내부 카테고리 ID") Long internalCategoryId,
        @Schema(description = "상태") String status,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt) {}
