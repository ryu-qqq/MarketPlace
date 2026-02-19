package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.query;

import io.swagger.v3.oas.annotations.Parameter;

/** 외부 카테고리 매핑 검색 요청 DTO. */
public record SearchExternalCategoryMappingsApiRequest(
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "페이지 번호 (0부터)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
