package com.ryuqq.marketplace.adapter.in.rest.exchange.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.ExchangeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ExchangeSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.mapper.ExchangeApiMapper;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;
import com.ryuqq.marketplace.application.exchange.port.in.query.GetExchangeDetailUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.query.GetExchangeListUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.query.GetExchangeSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 교환 조회 API 컨트롤러. */
@Tag(name = "교환 조회", description = "교환 조회 API")
@RestController
@RequestMapping(ExchangeAdminEndpoints.EXCHANGES)
public class ExchangeQueryController {

    private final GetExchangeSummaryUseCase getExchangeSummaryUseCase;
    private final GetExchangeListUseCase getExchangeListUseCase;
    private final GetExchangeDetailUseCase getExchangeDetailUseCase;
    private final ExchangeApiMapper mapper;

    public ExchangeQueryController(
            GetExchangeSummaryUseCase getExchangeSummaryUseCase,
            GetExchangeListUseCase getExchangeListUseCase,
            GetExchangeDetailUseCase getExchangeDetailUseCase,
            ExchangeApiMapper mapper) {
        this.getExchangeSummaryUseCase = getExchangeSummaryUseCase;
        this.getExchangeListUseCase = getExchangeListUseCase;
        this.getExchangeDetailUseCase = getExchangeDetailUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "교환 상태별 요약 조회", description = "교환 상태별 건수를 요약 조회합니다.")
    @PreAuthorize("@access.hasPermission('exchange:read')")
    @RequirePermission(value = "exchange:read", description = "교환 요약 조회")
    @GetMapping(ExchangeAdminEndpoints.SUMMARY)
    public ResponseEntity<ApiResponse<ExchangeSummaryApiResponse>> getSummary() {
        ExchangeSummaryResult result = getExchangeSummaryUseCase.execute();
        return ResponseEntity.ok(ApiResponse.of(mapper.toSummaryResponse(result)));
    }

    @Operation(summary = "교환 목록 조회", description = "교환 목록을 검색 조건으로 조회합니다.")
    @PreAuthorize("@access.hasPermission('exchange:read')")
    @RequirePermission(value = "exchange:read", description = "교환 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ExchangeListApiResponse>>> getList(
            ExchangeSearchApiRequest request) {
        ExchangePageResult result = getExchangeListUseCase.execute(mapper.toSearchParams(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(result)));
    }

    @Operation(summary = "교환 상세 조회", description = "교환 건의 상세 정보를 조회합니다.")
    @PreAuthorize("@access.hasPermission('exchange:read')")
    @RequirePermission(value = "exchange:read", description = "교환 상세 조회")
    @GetMapping(ExchangeAdminEndpoints.EXCHANGE_CLAIM_ID)
    public ResponseEntity<ApiResponse<ExchangeDetailApiResponse>> getDetail(
            @PathVariable String exchangeClaimId) {
        ExchangeDetailResult result = getExchangeDetailUseCase.execute(exchangeClaimId);
        return ResponseEntity.ok(ApiResponse.of(mapper.toDetailResponse(result)));
    }
}
