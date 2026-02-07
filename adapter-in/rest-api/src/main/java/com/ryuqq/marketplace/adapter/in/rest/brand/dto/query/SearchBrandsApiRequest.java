package com.ryuqq.marketplace.adapter.in.rest.brand.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 브랜드 검색 요청 DTO. */
public record SearchBrandsApiRequest(
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)") List<String> statuses,
        @Parameter(description = "검색 필드 (code, nameKo, nameEn)") String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "정렬 키 (createdAt, nameKo, updatedAt)") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
