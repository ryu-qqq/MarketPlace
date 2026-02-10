package com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 카테고리 매핑 조회 응답 DTO. */
@Schema(description = "카테고리 매핑 응답")
public record CategoryMappingApiResponse(
        @Schema(description = "카테고리 매핑 ID", example = "1") Long id,
        @Schema(description = "외부 채널 카테고리 ID", example = "1") Long salesChannelCategoryId,
        @Schema(description = "내부 카테고리 ID", example = "100") Long internalCategoryId,
        @Schema(description = "상태", example = "ACTIVE") String status,
        @Schema(description = "생성일시", example = "2025-01-23T10:30:00+09:00") String createdAt,
        @Schema(description = "수정일시", example = "2025-01-23T10:30:00+09:00") String updatedAt) {}
