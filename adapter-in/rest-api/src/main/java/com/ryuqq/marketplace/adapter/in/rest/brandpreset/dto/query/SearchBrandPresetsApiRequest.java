package com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 브랜드 프리셋 검색 API 요청 DTO. */
public record SearchBrandPresetsApiRequest(
        @Parameter(description = "판매채널 ID 목록", example = "1") List<Long> salesChannelIds,
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)", example = "ACTIVE")
                List<String> statuses,
        @Parameter(
                        description =
                                "검색 필드 (PRESET_NAME, SHOP_NAME, ACCOUNT_ID, BRAND_NAME,"
                                        + " BRAND_CODE)",
                        example = "PRESET_NAME")
                String searchField,
        @Parameter(description = "검색어", example = "나이키") String searchWord,
        @Parameter(description = "등록일 시작 (YYYY-MM-DD)", example = "2026-01-01") String startDate,
        @Parameter(description = "등록일 종료 (YYYY-MM-DD)", example = "2026-02-19") String endDate,
        @Parameter(description = "정렬 키 (CREATED_AT). 기본값: CREATED_AT", example = "CREATED_AT")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC). 기본값: DESC", example = "DESC")
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터). 기본값: 0", example = "0") Integer page,
        @Parameter(description = "페이지 크기. 기본값: 20", example = "20") Integer size) {}
