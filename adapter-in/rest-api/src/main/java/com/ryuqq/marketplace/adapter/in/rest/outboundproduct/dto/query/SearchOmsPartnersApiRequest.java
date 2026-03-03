package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query;

import io.swagger.v3.oas.annotations.Parameter;

/** OMS 파트너(셀러) 목록 검색 요청. */
public record SearchOmsPartnersApiRequest(
        @Parameter(description = "검색어 (셀러명)", example = "나이키") String keyword,
        @Parameter(description = "정렬 키 (CREATED_AT)", example = "CREATED_AT") String sortKey,
        @Parameter(description = "정렬 방향 (ASC/DESC)", example = "ASC") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터)", example = "0") Integer page,
        @Parameter(description = "페이지 크기", example = "100") Integer size) {}
