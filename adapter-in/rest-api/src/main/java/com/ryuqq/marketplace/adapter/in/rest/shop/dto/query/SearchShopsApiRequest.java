package com.ryuqq.marketplace.adapter.in.rest.shop.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** Shop 검색 API 요청 DTO. */
public record SearchShopsApiRequest(
        @Parameter(description = "판매채널 ID") Long salesChannelId,
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)") List<String> statuses,
        @Parameter(description = "검색 필드 (SHOP_NAME, ACCOUNT_ID)") String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "정렬 키 (createdAt, updatedAt, shopName)") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
