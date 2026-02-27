package com.ryuqq.marketplace.adapter.in.rest.order.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderSummaryApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 주문 조회 API 컨트롤러 (스텁). */
@Tag(name = "주문 조회", description = "주문 조회 API")
@RestController
@RequestMapping(OrderAdminEndpoints.ORDERS)
public class OrderQueryController {

    @Operation(summary = "주문 상태별 요약 조회", description = "주문 상태별 건수를 요약 조회합니다.")
    @PreAuthorize("@access.hasPermission('order:read')")
    @RequirePermission(value = "order:read", description = "주문 요약 조회")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<OrderSummaryApiResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.of(OrderSummaryApiResponse.empty()));
    }
}
