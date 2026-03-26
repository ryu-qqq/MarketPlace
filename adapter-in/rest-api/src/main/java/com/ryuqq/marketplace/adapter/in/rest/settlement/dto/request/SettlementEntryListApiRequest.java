package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 정산 원장 목록 조회 요청 DTO (GET 파라미터용). */
public record SettlementEntryListApiRequest(
        @Parameter(description = "정산 상태 필터 (PENDING, HOLD, COMPLETED)") List<String> status,
        @Parameter(description = "셀러 ID 목록 필터") List<Long> sellerIds,
        @Parameter(description = "검색 필드 (ORDER_ID, ORDER_NUMBER, PRODUCT_NAME, BUYER_NAME)")
                String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "시작일 (YYYY-MM-DD)", example = "2026-03-01") String startDate,
        @Parameter(description = "종료일 (YYYY-MM-DD)", example = "2026-03-31") String endDate,
        @NotNull @Parameter(description = "페이지 번호 (0부터)", example = "0") Integer page,
        @NotNull @Parameter(description = "페이지 크기", example = "20") Integer size) {}
