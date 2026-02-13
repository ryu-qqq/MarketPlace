package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 배송 검색 요청 DTO. */
public record ShipmentSearchApiRequest(
        @Parameter(
                        description =
                                "배송 상태 필터 (READY, PREPARING, SHIPPED, IN_TRANSIT, DELIVERED,"
                                        + " FAILED, CANCELLED)")
                List<String> statuses,
        @Parameter(description = "검색 필드 (shipmentNumber, orderNumber, trackingNumber)")
                String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "날짜 검색 대상 필드 (createdAt, shippedAt, deliveredAt)")
                String dateField,
        @Parameter(description = "정렬 키 (createdAt, shippedAt, deliveredAt)") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {}
