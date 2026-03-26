package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 카테고리 매핑 조회 응답 DTO. */
@Schema(description = "외부 카테고리 매핑 조회 응답")
public record InboundCategoryMappingApiResponse(
        @Schema(description = "매핑 ID", example = "1") Long id,
        @Schema(description = "외부 소스 ID", example = "1") Long inboundSourceId,
        @Schema(description = "외부 카테고리 코드", example = "NV_CAT_001") String externalCategoryCode,
        @Schema(description = "외부 카테고리명", example = "남성의류") String externalCategoryName,
        @Schema(description = "내부 카테고리 ID", example = "1") Long internalCategoryId,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status,
        @Schema(description = "생성일시 (KST)", example = "2025-01-23 10:30:00") String createdAt,
        @Schema(description = "수정일시 (KST)", example = "2025-01-23 10:30:00") String updatedAt) {}
