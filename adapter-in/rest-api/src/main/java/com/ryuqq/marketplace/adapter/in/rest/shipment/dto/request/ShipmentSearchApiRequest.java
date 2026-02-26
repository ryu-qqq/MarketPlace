package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 배송 검색 요청 DTO. */
public record ShipmentSearchApiRequest(
        @Parameter(
                        description =
                                "배송 상태 필터 (READY, PREPARING, SHIPPED, IN_TRANSIT, DELIVERED,"
                                        + " FAILED, CANCELLED)",
                        example = "READY")
                List<String> statuses,
        @Parameter(
                        description = "검색 필드 (ORDER_ID, TRACKING_NUMBER, CUSTOMER_NAME)",
                        example = "ORDER_ID")
                String searchField,
        @Parameter(description = "검색어", example = "ORD-001") String searchWord,
        @Parameter(
                        description = "날짜 검색 대상 필드 (PAYMENT, ORDER_CONFIRMED, SHIPPED)",
                        example = "PAYMENT")
                String dateField,
        @Parameter(
                        description =
                                "정렬 키 (CREATED_AT, SHIPPED_AT, DELIVERED_AT). 기본값: CREATED_AT",
                        example = "CREATED_AT")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC). 기본값: DESC", example = "DESC")
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터). 기본값: 0", example = "0") Integer page,
        @Parameter(description = "페이지 크기. 기본값: 20", example = "20") Integer size) {}
