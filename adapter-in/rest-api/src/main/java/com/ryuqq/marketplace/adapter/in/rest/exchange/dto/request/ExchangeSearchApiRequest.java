package com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 교환 목록 검색 요청. */
@Schema(description = "교환 목록 검색 요청")
public record ExchangeSearchApiRequest(
        @Schema(description = "교환 상태 필터") List<String> statuses,
        @Schema(description = "검색 필드 (CLAIM_NUMBER, ORDER_NUMBER 등)") String searchField,
        @Schema(description = "검색어") String searchWord,
        @Schema(description = "날짜 검색 대상 (REQUESTED, COMPLETED)") String dateField,
        @Schema(description = "시작일 (YYYY-MM-DD)") String startDate,
        @Schema(description = "종료일 (YYYY-MM-DD)") String endDate,
        @Schema(description = "정렬 키 (CREATED_AT, REQUESTED_AT, COMPLETED_AT)") String sortKey,
        @Schema(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Schema(description = "페이지 번호 (0부터)") Integer page,
        @Schema(description = "페이지 크기") Integer size) {

    public int resolvedPage() {
        return page != null ? page : 0;
    }

    public int resolvedSize() {
        return size != null ? size : 20;
    }
}
