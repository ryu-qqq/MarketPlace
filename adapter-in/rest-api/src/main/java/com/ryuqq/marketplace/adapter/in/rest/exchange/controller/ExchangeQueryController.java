package com.ryuqq.marketplace.adapter.in.rest.exchange.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.ExchangeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeSummaryApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 교환 조회 API 컨트롤러 (스텁). */
@Tag(name = "교환 조회", description = "교환 조회 API")
@RestController
@RequestMapping(ExchangeAdminEndpoints.EXCHANGES)
public class ExchangeQueryController {

    @Operation(summary = "교환 상태별 요약 조회", description = "교환 상태별 건수를 요약 조회합니다.")
    @PreAuthorize("@access.hasPermission('exchange:read')")
    @RequirePermission(value = "exchange:read", description = "교환 요약 조회")
    @GetMapping(ExchangeAdminEndpoints.SUMMARY)
    public ResponseEntity<ApiResponse<ExchangeSummaryApiResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.of(ExchangeSummaryApiResponse.empty()));
    }
}
