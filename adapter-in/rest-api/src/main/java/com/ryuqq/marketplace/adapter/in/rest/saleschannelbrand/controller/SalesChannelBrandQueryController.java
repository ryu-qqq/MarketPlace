package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.SalesChannelBrandAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.query.SearchSalesChannelBrandsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.response.SalesChannelBrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.mapper.SalesChannelBrandQueryApiMapper;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.query.SalesChannelBrandSearchParams;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;
import com.ryuqq.marketplace.application.saleschannelbrand.port.in.query.SearchSalesChannelBrandByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 외부채널 브랜드 조회 Controller. */
@Tag(name = "외부채널 브랜드 조회", description = "외부채널 브랜드 조회 API")
@RestController
@RequestMapping(SalesChannelBrandAdminEndpoints.BRANDS)
public class SalesChannelBrandQueryController {

    private final SearchSalesChannelBrandByOffsetUseCase searchUseCase;
    private final SalesChannelBrandQueryApiMapper mapper;

    public SalesChannelBrandQueryController(
            SearchSalesChannelBrandByOffsetUseCase searchUseCase,
            SalesChannelBrandQueryApiMapper mapper) {
        this.searchUseCase = searchUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부채널 브랜드 목록 조회", description = "외부채널 브랜드 목록을 복합 조건으로 조회합니다.")
    @PreAuthorize("@access.hasPermission('sales-channel-brand:read')")
    @RequirePermission(value = "sales-channel-brand:read", description = "외부채널 브랜드 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<SalesChannelBrandApiResponse>>> searchBrands(
            @Parameter(description = "판매채널 ID", required = true)
                    @PathVariable(SalesChannelBrandAdminEndpoints.PATH_SALES_CHANNEL_ID)
                    Long salesChannelId,
            @ParameterObject @Valid SearchSalesChannelBrandsApiRequest request) {

        SalesChannelBrandSearchParams params =
                mapper.toSearchParams(List.of(salesChannelId), request);
        SalesChannelBrandPageResult pageResult = searchUseCase.execute(params);
        PageApiResponse<SalesChannelBrandApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
