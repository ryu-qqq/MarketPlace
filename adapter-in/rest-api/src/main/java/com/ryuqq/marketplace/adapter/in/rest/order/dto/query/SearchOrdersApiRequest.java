package com.ryuqq.marketplace.adapter.in.rest.order.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.util.List;

/**
 * 주문 목록 검색 요청 DTO.
 *
 * <p>프론트엔드 호출 예시: {@code
 * /orders?startDate=2026-03-02&endDate=2026-03-02&dateField=ORDERED&status=PREPARING&page=0&size=20}
 */
public record SearchOrdersApiRequest(
        @Parameter(description = "날짜 검색 대상 (ORDERED, SHIPPED, DELIVERED)", example = "ORDERED")
                String dateField,
        @Parameter(description = "시작일 (YYYY-MM-DD)", example = "2026-03-01") LocalDate startDate,
        @Parameter(description = "종료일 (YYYY-MM-DD)", example = "2026-03-31") LocalDate endDate,
        @Parameter(description = "주문 상태 필터", example = "ORDERED") List<String> status,
        @Parameter(description = "검색 필드 (ORDER_ID, ORDER_NUMBER, CUSTOMER_NAME, PRODUCT_NAME)")
                String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(
                        description = "정렬 키 (CREATED_AT, ORDERED_AT, UPDATED_AT). 기본값: CREATED_AT",
                        example = "CREATED_AT")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC). 기본값: DESC", example = "DESC")
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터). 기본값: 0", example = "0") Integer page,
        @Parameter(description = "페이지 크기. 기본값: 20", example = "20") Integer size) {}
