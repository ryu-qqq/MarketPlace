package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 외부채널 브랜드 검색 API 요청 DTO. */
public record SearchSalesChannelBrandsApiRequest(
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)") List<String> statuses,
        @Parameter(description = "검색 필드 (externalBrandCode, externalBrandName)") String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "정렬 키 (createdAt, externalBrandName)") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
