package com.ryuqq.marketplace.adapter.in.rest.cancel.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.cancel.CancelAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 취소 조회 API 컨트롤러 (스텁). */
@Tag(name = "취소 조회", description = "취소 조회 API")
@RestController
@RequestMapping(CancelAdminEndpoints.CANCELS)
public class CancelQueryController {

    @Operation(summary = "취소 상태별 요약 조회", description = "취소 상태별 건수를 요약 조회합니다.")
    @PreAuthorize("@access.hasPermission('cancel:read')")
    @RequirePermission(value = "cancel:read", description = "취소 요약 조회")
    @GetMapping(CancelAdminEndpoints.SUMMARY)
    public ResponseEntity<ApiResponse<CancelSummaryApiResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.of(CancelSummaryApiResponse.empty()));
    }
}
