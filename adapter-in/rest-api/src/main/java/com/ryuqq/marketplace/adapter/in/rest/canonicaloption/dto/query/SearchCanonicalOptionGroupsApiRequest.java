package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.query;

import io.swagger.v3.oas.annotations.Parameter;

/** 캐노니컬 옵션 그룹 검색 요청 DTO. */
public record SearchCanonicalOptionGroupsApiRequest(
        @Parameter(description = "활성 상태 필터 (true/false, 미지정시 전체)", example = "true") Boolean active,
        @Parameter(description = "검색 필드 (CODE, NAME_KO, NAME_EN)", example = "NAME_KO")
                String searchField,
        @Parameter(description = "검색어", example = "색상") String searchWord,
        @Parameter(description = "정렬 키 (CREATED_AT, CODE). 기본값: CREATED_AT", example = "CREATED_AT")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC). 기본값: DESC", example = "DESC")
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터). 기본값: 0", example = "0") Integer page,
        @Parameter(description = "페이지 크기. 기본값: 20", example = "20") Integer size) {}
