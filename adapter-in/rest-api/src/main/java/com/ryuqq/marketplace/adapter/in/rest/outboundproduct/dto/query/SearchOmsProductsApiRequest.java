package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** OMS 상품 목록 검색 요청. */
public record SearchOmsProductsApiRequest(
        @Parameter(description = "날짜 필터 대상 (CREATED_AT/UPDATED_AT)", example = "CREATED_AT")
                String dateType,
        @Parameter(description = "조회 시작일 (yyyy-MM-dd)", example = "2026-01-01") String startDate,
        @Parameter(description = "조회 종료일 (yyyy-MM-dd)", example = "2026-03-03") String endDate,
        @Parameter(description = "상품 상태 필터 (ACTIVE/INACTIVE/SOLDOUT)", example = "ACTIVE")
                List<String> statuses,
        @Parameter(description = "연동 상태 필터 (SUCCESS/FAILED/PENDING)", example = "FAILED")
                List<String> syncStatuses,
        @Parameter(
                        description =
                                "검색 필드 (productCode: 상품코드 / productName: 상품명 / partnerName: 파트너명)",
                        example = "productName")
                String searchField,
        @Parameter(description = "검색어", example = "나이키") String searchWord,
        @Parameter(description = "쇼핑몰 ID 목록") List<Long> shopIds,
        @Parameter(description = "파트너(셀러) ID 목록") List<Long> partnerIds,
        @Parameter(description = "상품 코드 목록") List<String> productCodes,
        @Parameter(
                        description = "정렬 키 (CREATED_AT/UPDATED_AT/PRODUCT_GROUP_NAME)",
                        example = "CREATED_AT")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC/DESC)", example = "DESC") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터)", example = "0") Integer page,
        @Parameter(description = "페이지 크기", example = "10") Integer size) {}
