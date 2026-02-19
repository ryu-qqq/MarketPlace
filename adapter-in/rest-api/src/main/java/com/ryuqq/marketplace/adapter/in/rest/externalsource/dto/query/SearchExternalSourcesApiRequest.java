package com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 외부 소스 검색 요청 DTO. */
public record SearchExternalSourcesApiRequest(
        @Parameter(description = "유형 필터 (CRAWLING, LEGACY, PARTNER)", example = "CRAWLING")
                List<String> types,
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)", example = "ACTIVE")
                List<String> statuses,
        @Parameter(description = "검색 필드 (CODE, NAME)", example = "NAME") String searchField,
        @Parameter(description = "검색어", example = "네이버") String searchWord,
        @Parameter(description = "정렬 기준 (CREATED_AT). 기본값: CREATED_AT", example = "CREATED_AT")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC). 기본값: DESC", example = "DESC")
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터). 기본값: 0", example = "0") Integer page,
        @Parameter(description = "페이지 크기. 기본값: 20", example = "20") Integer size) {}
