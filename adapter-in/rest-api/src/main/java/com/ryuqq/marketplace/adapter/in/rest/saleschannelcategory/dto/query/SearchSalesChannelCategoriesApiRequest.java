package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 외부채널 카테고리 검색 API 요청 DTO. */
public record SearchSalesChannelCategoriesApiRequest(
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)", example = "ACTIVE")
                List<String> statuses,
        @Parameter(description = "검색 필드 (EXTERNAL_CODE, EXTERNAL_NAME)", example = "EXTERNAL_NAME")
                String searchField,
        @Parameter(description = "검색어", example = "의류") String searchWord,
        @Parameter(description = "카테고리 깊이", example = "0") Integer depth,
        @Parameter(description = "부모 카테고리 ID", example = "0") Long parentId,
        @Parameter(description = "내부 카테고리 매핑 여부", example = "true") Boolean mapped,
        @Parameter(
                        description =
                                "정렬 키 (CREATED_AT, EXTERNAL_NAME, SORT_ORDER, 기본값: SORT_ORDER)",
                        example = "SORT_ORDER")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC, 기본값: DESC)", example = "DESC")
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)", example = "0") Integer page,
        @Parameter(description = "페이지 크기 (기본값: 20)", example = "20") Integer size) {}
