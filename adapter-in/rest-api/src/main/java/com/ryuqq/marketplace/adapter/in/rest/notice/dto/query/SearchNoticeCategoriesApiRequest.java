package com.ryuqq.marketplace.adapter.in.rest.notice.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/** 고시정보 카테고리 검색 요청 DTO. */
@Schema(description = "고시정보 카테고리 검색 요청")
public record SearchNoticeCategoriesApiRequest(
        @Parameter(description = "활성 상태 필터 (true/false, 미지정시 전체)", example = "true")
                @Schema(description = "활성 상태 필터", nullable = true)
                Boolean active,
        @Parameter(
                        description = "검색 필드 (CODE, NAME_KO, NAME_EN)",
                        example = "CODE",
                        schema = @Schema(allowableValues = {"CODE", "NAME_KO", "NAME_EN"}))
                @Schema(description = "검색 필드", nullable = true)
                String searchField,
        @Parameter(description = "검색어", example = "CLOTHING")
                @Schema(description = "검색어", nullable = true)
                String searchWord,
        @Parameter(
                        description = "정렬 키 (CREATED_AT, CODE). 기본값: CREATED_AT",
                        example = "CREATED_AT",
                        schema = @Schema(allowableValues = {"CREATED_AT", "CODE"}))
                @Schema(description = "정렬 키", nullable = true)
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC). 기본값: DESC", example = "DESC")
                @Schema(description = "정렬 방향", nullable = true)
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터). 기본값: 0", example = "0")
                @Schema(description = "페이지 번호 (0부터 시작)", minimum = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
                Integer page,
        @Parameter(description = "페이지 크기. 기본값: 20", example = "20")
                @Schema(description = "페이지 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
                Integer size) {}
