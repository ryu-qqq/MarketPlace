package com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 외부 소스 검색 요청 DTO. */
public record SearchExternalSourcesApiRequest(
        @Parameter(description = "유형 필터 (SALES_CHANNEL, SUPPLIER 등)") List<String> types,
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)") List<String> statuses,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "페이지 번호 (0부터)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
