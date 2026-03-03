package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query;

import io.swagger.v3.oas.annotations.Parameter;

/** OMS 쇼핑몰 목록 검색 요청. */
public record SearchOmsShopsApiRequest(
        @Parameter(description = "검색어 (쇼핑몰명)", example = "스마트스토어") String keyword,
        @Parameter(description = "정렬 키 (CREATED_AT)", example = "CREATED_AT") String sortKey,
        @Parameter(description = "정렬 방향 (ASC/DESC)", example = "ASC") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터)", example = "0") Integer page,
        @Parameter(description = "페이지 크기", example = "100") Integer size) {}
