package com.ryuqq.marketplace.adapter.in.rest.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 카테고리 조회 API 응답 DTO. */
@Schema(description = "카테고리 조회 응답")
public record CategoryApiResponse(
        @Schema(description = "카테고리 ID", example = "1") Long id,
        @Schema(description = "카테고리 코드", example = "CAT001") String code,
        @Schema(description = "한글명", example = "패션의류") String nameKo,
        @Schema(description = "영문명", example = "Fashion Clothing") String nameEn,
        @Schema(description = "부모 카테고리 ID", example = "1") Long parentId,
        @Schema(description = "계층 깊이", example = "0") int depth,
        @Schema(description = "경로", example = "1") String path,
        @Schema(description = "정렬 순서", example = "1") int sortOrder,
        @Schema(description = "리프 노드 여부", example = "false") boolean leaf,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status,
        @Schema(
                        description =
                                "부문 (FASHION, BEAUTY, LIVING, FOOD, DIGITAL, SPORTS, KIDS,"
                                        + " PET, CULTURE, HEALTH, ETC)",
                        example = "FASHION")
                String department,
        @Schema(
                        description =
                                "카테고리 그룹 - 고시정보 연결용 (CLOTHING, SHOES, BAGS,"
                                        + " ACCESSORIES, COSMETICS, JEWELRY, WATCHES, FURNITURE,"
                                        + " DIGITAL, SPORTS, BABY_KIDS, ETC)",
                        example = "CLOTHING")
                String categoryGroup,
        @Schema(description = "표시용 이름 경로", example = "패션의류 > 남성의류 > 티셔츠") String displayPath,
        @Schema(description = "생성일시 (KST)", example = "2026-01-15T09:30:00Z") String createdAt,
        @Schema(description = "수정일시 (KST)", example = "2026-02-10T14:20:00Z") String updatedAt) {}
