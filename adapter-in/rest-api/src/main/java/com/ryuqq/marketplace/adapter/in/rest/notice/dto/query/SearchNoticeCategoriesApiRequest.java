package com.ryuqq.marketplace.adapter.in.rest.notice.dto.query;

import io.swagger.v3.oas.annotations.Parameter;

/** 고시정보 카테고리 검색 요청 DTO. */
public record SearchNoticeCategoriesApiRequest(
        @Parameter(description = "활성 상태 필터 (true/false, 미지정시 전체)") Boolean active,
        @Parameter(description = "검색 필드 (CODE, NAME_KO, NAME_EN)") String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "정렬 키 (createdAt, code)") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
