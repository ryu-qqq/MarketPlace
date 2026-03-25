package com.ryuqq.marketplace.adapter.in.rest.notice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 고시정보 카테고리 API 응답 DTO. */
@Schema(description = "고시정보 카테고리 응답")
public record NoticeCategoryApiResponse(
        @Schema(description = "카테고리 ID", example = "1") Long id,
        @Schema(description = "카테고리 코드", example = "CLOTHING") String code,
        @Schema(description = "한글명", example = "의류") String nameKo,
        @Schema(description = "영문명", example = "Clothing") String nameEn,
        @Schema(description = "대상 카테고리 그룹", example = "CLOTHING") String targetCategoryGroup,
        @Schema(description = "활성 상태", example = "true") boolean active,
        @Schema(description = "고시정보 필드 목록") List<NoticeFieldApiResponse> fields,
        @Schema(description = "생성일시 (KST)", example = "2026-01-15T10:30:00Z")
                String createdAt) {}
