package com.ryuqq.marketplace.adapter.in.rest.cancel.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.cancel.CancelAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.CancelSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.mapper.CancelApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ClaimOrderEnricher;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelDetailUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelListUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 취소 조회 API 컨트롤러. */
@Tag(name = "취소 조회", description = "취소 조회 API")
@RestController
@RequestMapping(CancelAdminEndpoints.CANCELS)
public class CancelQueryController {

    private final GetCancelSummaryUseCase getCancelSummaryUseCase;
    private final GetCancelListUseCase getCancelListUseCase;
    private final GetCancelDetailUseCase getCancelDetailUseCase;
    private final CancelApiMapper mapper;
    private final ClaimOrderEnricher enricher;

    public CancelQueryController(
            GetCancelSummaryUseCase getCancelSummaryUseCase,
            GetCancelListUseCase getCancelListUseCase,
            GetCancelDetailUseCase getCancelDetailUseCase,
            CancelApiMapper mapper,
            ClaimOrderEnricher enricher) {
        this.getCancelSummaryUseCase = getCancelSummaryUseCase;
        this.getCancelListUseCase = getCancelListUseCase;
        this.getCancelDetailUseCase = getCancelDetailUseCase;
        this.mapper = mapper;
        this.enricher = enricher;
    }

    @Operation(summary = "취소 상태별 요약 조회", description = "취소 상태별 건수를 요약 조회합니다.")
    @PreAuthorize("@access.hasPermission('cancel:read')")
    @RequirePermission(value = "cancel:read", description = "취소 요약 조회")
    @GetMapping(CancelAdminEndpoints.SUMMARY)
    public ResponseEntity<ApiResponse<CancelSummaryApiResponse>> getSummary() {
        CancelSummaryResult result = getCancelSummaryUseCase.execute();
        return ResponseEntity.ok(ApiResponse.of(mapper.toSummaryResponse(result)));
    }

    @Operation(summary = "취소 목록 조회", description = "취소 목록을 검색 조건으로 조회합니다.")
    @PreAuthorize("@access.hasPermission('cancel:read')")
    @RequirePermission(value = "cancel:read", description = "취소 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ClaimListItemApiResponseV4>>> getList(
            CancelSearchApiRequest request) {
        CancelPageResult result = getCancelListUseCase.execute(mapper.toSearchParams(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponseV4(result, enricher)));
    }

    @Operation(summary = "취소 상세 조회", description = "취소 건의 상세 정보를 조회합니다.")
    @PreAuthorize("@access.hasPermission('cancel:read')")
    @RequirePermission(value = "cancel:read", description = "취소 상세 조회")
    @GetMapping(CancelAdminEndpoints.CANCEL_ID)
    public ResponseEntity<ApiResponse<CancelDetailApiResponse>> getDetail(
            @PathVariable String cancelId) {
        CancelDetailResult result = getCancelDetailUseCase.execute(cancelId);
        return ResponseEntity.ok(ApiResponse.of(mapper.toDetailResponse(result)));
    }
}
