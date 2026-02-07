package com.ryuqq.marketplace.adapter.in.rest.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 카테고리 조회 API 응답 DTO. */
@Schema(description = "카테고리 조회 응답")
public record CategoryApiResponse(
        @Schema(description = "카테고리 ID") Long id,
        @Schema(description = "카테고리 코드") String code,
        @Schema(description = "한글명") String nameKo,
        @Schema(description = "영문명") String nameEn,
        @Schema(description = "부모 카테고리 ID") Long parentId,
        @Schema(description = "계층 깊이") int depth,
        @Schema(description = "경로") String path,
        @Schema(description = "정렬 순서") int sortOrder,
        @Schema(description = "리프 노드 여부") boolean leaf,
        @Schema(description = "상태") String status,
        @Schema(description = "부문") String department,
        @Schema(description = "카테고리 그룹 (고시정보 연결용)") String categoryGroup,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt) {}
