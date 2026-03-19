package com.ryuqq.marketplace.adapter.in.rest.settlement.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.SettlementAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.DailySettlementApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementEntryListApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.mapper.SettlementApiMapper;
import com.ryuqq.marketplace.application.settlement.dto.query.DailySettlementSearchParams;
import com.ryuqq.marketplace.application.settlement.dto.response.DailySettlementResult;
import com.ryuqq.marketplace.application.settlement.dto.response.SettlementEntryPageResult;
import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;
import com.ryuqq.marketplace.application.settlement.entry.port.in.query.GetSettlementEntryListUseCase;
import com.ryuqq.marketplace.application.settlement.port.in.query.GetDailySettlementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 정산 조회 API 컨트롤러. */
@Tag(name = "정산 조회", description = "정산 조회 API")
@RestController
@RequestMapping(SettlementAdminEndpoints.SETTLEMENTS)
public class SettlementQueryController {

    private final GetSettlementEntryListUseCase getSettlementEntryListUseCase;
    private final GetDailySettlementUseCase getDailySettlementUseCase;
    private final SettlementApiMapper mapper;

    public SettlementQueryController(
            GetSettlementEntryListUseCase getSettlementEntryListUseCase,
            GetDailySettlementUseCase getDailySettlementUseCase,
            SettlementApiMapper mapper) {
        this.getSettlementEntryListUseCase = getSettlementEntryListUseCase;
        this.getDailySettlementUseCase = getDailySettlementUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "정산 대상 목록 조회", description = "정산 원장(Entry) 기준으로 목록을 페이지 조회합니다.")
    @PreAuthorize("@access.hasPermission('settlement:read')")
    @RequirePermission(value = "settlement:read", description = "정산 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<SettlementListItemApiResponse>>>
            getSettlements(@Valid @ParameterObject SettlementEntryListApiRequest request) {

        SettlementEntrySearchParams params = mapper.toSearchParams(request);
        SettlementEntryPageResult result = getSettlementEntryListUseCase.execute(params);
        PageApiResponse<SettlementListItemApiResponse> response = mapper.toPageResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "일별 정산 내역 조회", description = "기간별 일별 정산 통계를 조회합니다.")
    @PreAuthorize("@access.hasPermission('settlement:read')")
    @RequirePermission(value = "settlement:read", description = "일별 정산 조회")
    @GetMapping(SettlementAdminEndpoints.DAILY)
    public ResponseEntity<ApiResponse<PageApiResponse<DailySettlementApiResponse>>> getDaily(
            @Valid @ParameterObject DailySettlementApiRequest request) {

        DailySettlementSearchParams params = mapper.toDailySearchParams(request);
        java.util.List<DailySettlementResult> results = getDailySettlementUseCase.execute(params);
        PageApiResponse<DailySettlementApiResponse> response =
                mapper.toDailyPageResponse(results, request.page(), request.size());

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
