package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query;

import io.swagger.v3.oas.annotations.Parameter;

/** 연동 이력 검색 요청. */
public record SearchSyncHistoryApiRequest(
        @Parameter(description = "상태 필터 (PENDING/PROCESSING/COMPLETED/FAILED)", example = "FAILED")
                String status,
        @Parameter(description = "페이지 번호 (0부터)", example = "0") Integer page,
        @Parameter(description = "페이지 크기", example = "10") Integer size) {}
