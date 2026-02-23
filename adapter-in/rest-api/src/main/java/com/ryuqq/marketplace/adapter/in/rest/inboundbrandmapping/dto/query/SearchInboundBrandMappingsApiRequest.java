package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.query;

import io.swagger.v3.oas.annotations.Parameter;

/** 외부 브랜드 매핑 검색 요청 DTO. */
public record SearchInboundBrandMappingsApiRequest(
        @Parameter(description = "검색 필드 (EXTERNAL_CODE, EXTERNAL_NAME)", example = "EXTERNAL_NAME")
                String searchField,
        @Parameter(description = "검색어", example = "나이키") String searchWord,
        @Parameter(description = "정렬 기준 (CREATED_AT). 기본값: CREATED_AT", example = "CREATED_AT")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC). 기본값: DESC", example = "DESC")
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터). 기본값: 0", example = "0") Integer page,
        @Parameter(description = "페이지 크기. 기본값: 20", example = "20") Integer size) {}
