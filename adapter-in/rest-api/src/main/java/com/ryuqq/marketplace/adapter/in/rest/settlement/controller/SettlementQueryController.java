package com.ryuqq.marketplace.adapter.in.rest.settlement.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.SettlementAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 정산 조회 API 컨트롤러 (스텁). */
@Tag(name = "정산 조회", description = "정산 조회 API")
@RestController
@RequestMapping(SettlementAdminEndpoints.SETTLEMENTS)
public class SettlementQueryController {

    @Operation(summary = "일별 정산 내역 조회", description = "기간별 일별 정산 통계를 조회합니다.")
    @PreAuthorize("@access.hasPermission('settlement:read')")
    @RequirePermission(value = "settlement:read", description = "일별 정산 조회")
    @GetMapping(SettlementAdminEndpoints.DAILY)
    public ResponseEntity<ApiResponse<DailySettlementApiResponse>> getDaily() {
        return ResponseEntity.ok(ApiResponse.of(DailySettlementApiResponse.empty()));
    }
}
