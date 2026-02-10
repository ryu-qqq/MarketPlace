package com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 카테고리 매핑 검색 API 요청 DTO. */
public record SearchCategoryMappingsApiRequest(
        @Parameter(description = "외부 채널 카테고리 ID 목록") List<Long> salesChannelCategoryIds,
        @Parameter(description = "내부 카테고리 ID 목록") List<Long> internalCategoryIds,
        @Parameter(description = "판매채널 ID 목록") List<Long> salesChannelIds,
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)") List<String> statuses,
        @Parameter(description = "검색 필드 (EXTERNAL_CATEGORY_NAME, INTERNAL_CATEGORY_NAME)")
                String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "정렬 키 (createdAt)") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
