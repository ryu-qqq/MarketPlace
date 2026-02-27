package com.ryuqq.marketplace.adapter.in.rest.refund.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.RefundAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundSummaryApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 환불 조회 API 컨트롤러 (스텁). */
@Tag(name = "환불 조회", description = "환불 조회 API")
@RestController
@RequestMapping(RefundAdminEndpoints.REFUNDS)
public class RefundQueryController {

    @Operation(summary = "환불 상태별 요약 조회", description = "환불 상태별 건수를 요약 조회합니다.")
    @PreAuthorize("@access.hasPermission('refund:read')")
    @RequirePermission(value = "refund:read", description = "환불 요약 조회")
    @GetMapping(RefundAdminEndpoints.SUMMARY)
    public ResponseEntity<ApiResponse<RefundSummaryApiResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.of(RefundSummaryApiResponse.empty()));
    }
}
