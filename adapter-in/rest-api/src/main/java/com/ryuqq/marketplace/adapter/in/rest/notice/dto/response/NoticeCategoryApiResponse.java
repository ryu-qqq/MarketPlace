package com.ryuqq.marketplace.adapter.in.rest.notice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 고시정보 카테고리 API 응답 DTO. */
@Schema(description = "고시정보 카테고리 응답")
public record NoticeCategoryApiResponse(
        @Schema(description = "카테고리 ID") Long id,
        @Schema(description = "카테고리 코드") String code,
        @Schema(description = "한글명") String nameKo,
        @Schema(description = "영문명") String nameEn,
        @Schema(description = "대상 카테고리 그룹") String targetCategoryGroup,
        @Schema(description = "활성 상태") boolean active,
        @Schema(description = "고시정보 필드 목록") List<NoticeFieldApiResponse> fields,
        @Schema(description = "생성일시") String createdAt) {}
