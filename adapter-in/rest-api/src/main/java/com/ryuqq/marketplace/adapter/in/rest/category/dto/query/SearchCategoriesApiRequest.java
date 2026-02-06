package com.ryuqq.marketplace.adapter.in.rest.category.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 카테고리 검색 요청 DTO. */
public record SearchCategoriesApiRequest(
        @Parameter(description = "부모 카테고리 ID") Long parentId,
        @Parameter(description = "계층 깊이") Integer depth,
        @Parameter(description = "리프 노드 여부") Boolean leaf,
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)") List<String> statuses,
        @Parameter(description = "부문 필터 (FASHION, BEAUTY, LIVING 등)") List<String> departments,
        @Parameter(description = "카테고리 그룹 필터 (CLOTHING, SHOES, DIGITAL 등 - 고시정보 연결용)")
                List<String> categoryGroups,
        @Parameter(description = "검색 필드 (code, nameKo, nameEn)") String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "정렬 키 (sortOrder, createdAt, nameKo, code)") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
