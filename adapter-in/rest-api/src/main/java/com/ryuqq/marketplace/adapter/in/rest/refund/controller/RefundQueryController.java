package com.ryuqq.marketplace.adapter.in.rest.refund.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ClaimOrderEnricher;
import com.ryuqq.marketplace.adapter.in.rest.refund.RefundAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RefundSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.mapper.RefundApiMapper;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundDetailUseCase;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundListUseCase;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 환불 조회 API 컨트롤러. */
@Tag(name = "환불 조회", description = "환불 조회 API")
@RestController
@RequestMapping(RefundAdminEndpoints.REFUNDS)
public class RefundQueryController {

    private final GetRefundSummaryUseCase getRefundSummaryUseCase;
    private final GetRefundListUseCase getRefundListUseCase;
    private final GetRefundDetailUseCase getRefundDetailUseCase;
    private final RefundApiMapper mapper;
    private final ClaimOrderEnricher enricher;

    public RefundQueryController(
            GetRefundSummaryUseCase getRefundSummaryUseCase,
            GetRefundListUseCase getRefundListUseCase,
            GetRefundDetailUseCase getRefundDetailUseCase,
            RefundApiMapper mapper,
            ClaimOrderEnricher enricher) {
        this.getRefundSummaryUseCase = getRefundSummaryUseCase;
        this.getRefundListUseCase = getRefundListUseCase;
        this.getRefundDetailUseCase = getRefundDetailUseCase;
        this.mapper = mapper;
        this.enricher = enricher;
    }

    @Operation(summary = "환불 상태별 요약 조회", description = "환불 상태별 건수를 요약 조회합니다.")
    @PreAuthorize("@access.hasPermission('refund:read')")
    @RequirePermission(value = "refund:read", description = "환불 요약 조회")
    @GetMapping(RefundAdminEndpoints.SUMMARY)
    public ResponseEntity<ApiResponse<RefundSummaryApiResponse>> getSummary() {
        RefundSummaryResult result = getRefundSummaryUseCase.execute();
        return ResponseEntity.ok(ApiResponse.of(mapper.toSummaryResponse(result)));
    }

    @Operation(summary = "환불 목록 조회", description = "환불 목록을 검색 조건으로 조회합니다.")
    @PreAuthorize("@access.hasPermission('refund:read')")
    @RequirePermission(value = "refund:read", description = "환불 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ClaimListItemApiResponseV4>>> getList(
            RefundSearchApiRequest request) {
        RefundPageResult result = getRefundListUseCase.execute(mapper.toSearchParams(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponseV4(result, enricher)));
    }

    @Operation(summary = "환불 상세 조회", description = "환불 건의 상세 정보를 조회합니다.")
    @PreAuthorize("@access.hasPermission('refund:read')")
    @RequirePermission(value = "refund:read", description = "환불 상세 조회")
    @GetMapping(RefundAdminEndpoints.REFUND_CLAIM_ID)
    public ResponseEntity<ApiResponse<RefundDetailApiResponse>> getDetail(
            @PathVariable String refundClaimId) {
        RefundDetailResult result = getRefundDetailUseCase.execute(refundClaimId);
        return ResponseEntity.ok(ApiResponse.of(mapper.toDetailResponse(result)));
    }
}
