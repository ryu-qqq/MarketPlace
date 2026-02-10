package com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 브랜드 프리셋 검색 API 요청 DTO. */
public record SearchBrandPresetsApiRequest(
        @Parameter(description = "판매채널 ID 목록") List<Long> salesChannelIds,
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)") List<String> statuses,
        @Parameter(
                        description =
                                "검색 필드 (PRESET_NAME, SHOP_NAME, ACCOUNT_ID, BRAND_NAME,"
                                        + " BRAND_CODE)")
                String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "등록일 시작 (YYYY-MM-DD)") String startDate,
        @Parameter(description = "등록일 종료 (YYYY-MM-DD)") String endDate,
        @Parameter(description = "정렬 키 (createdAt)") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
