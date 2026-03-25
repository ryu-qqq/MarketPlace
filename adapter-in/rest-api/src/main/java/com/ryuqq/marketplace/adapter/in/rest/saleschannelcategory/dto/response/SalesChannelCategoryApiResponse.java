package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부채널 카테고리 조회 응답 DTO. */
@Schema(description = "외부채널 카테고리 응답")
public record SalesChannelCategoryApiResponse(
        @Schema(description = "카테고리 ID", example = "1") Long id,
        @Schema(description = "판매채널 ID", example = "1") Long salesChannelId,
        @Schema(description = "외부 카테고리 코드", example = "CAT001") String externalCategoryCode,
        @Schema(description = "외부 카테고리명", example = "의류") String externalCategoryName,
        @Schema(description = "부모 카테고리 ID", example = "0") Long parentId,
        @Schema(description = "카테고리 깊이", example = "0") int depth,
        @Schema(description = "카테고리 경로", example = "1") String path,
        @Schema(description = "정렬 순서", example = "1") int sortOrder,
        @Schema(description = "리프 노드 여부", example = "false") boolean leaf,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status,
        @Schema(description = "생성일시", example = "2025-01-23 10:30:00") String createdAt,
        @Schema(description = "수정일시", example = "2025-01-23 10:30:00") String updatedAt) {}
