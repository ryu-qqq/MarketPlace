package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 외부채널 카테고리 검색 API 요청 DTO. */
public record SearchSalesChannelCategoriesApiRequest(
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)") List<String> statuses,
        @Parameter(description = "검색 필드 (externalCategoryCode, externalCategoryName)")
                String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "카테고리 깊이") Integer depth,
        @Parameter(description = "부모 카테고리 ID") Long parentId,
        @Parameter(description = "내부 카테고리 매핑 여부") Boolean mapped,
        @Parameter(description = "정렬 키 (createdAt, externalCategoryName, sortOrder)")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
