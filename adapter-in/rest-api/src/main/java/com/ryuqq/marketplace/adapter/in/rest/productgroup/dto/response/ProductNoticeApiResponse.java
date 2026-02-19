package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 상품 고시정보 API 응답 DTO. */
@Schema(description = "상품 고시정보 응답")
public record ProductNoticeApiResponse(
        @Schema(description = "고시정보 ID", example = "1") Long id,
        @Schema(description = "고시정보 카테고리 ID", example = "1") Long noticeCategoryId,
        @Schema(description = "고시정보 항목 목록") List<ProductNoticeEntryApiResponse> entries,
        @Schema(description = "생성일시 (ISO 8601)", example = "2026-01-15T10:30:00Z") String createdAt,
        @Schema(description = "수정일시 (ISO 8601)", example = "2026-01-20T14:00:00Z")
                String updatedAt) {}
