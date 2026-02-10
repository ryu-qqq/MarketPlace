package com.ryuqq.marketplace.adapter.in.rest.saleschannel.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.SalesChannelAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.query.SearchSalesChannelsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.response.SalesChannelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.mapper.SalesChannelQueryApiMapper;
import com.ryuqq.marketplace.application.saleschannel.dto.query.SalesChannelSearchParams;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.port.in.query.SearchSalesChannelByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 판매채널 조회 Controller. */
@Tag(name = "판매채널 조회", description = "판매채널 조회 API")
@RestController
@RequestMapping(SalesChannelAdminEndpoints.SALES_CHANNELS)
public class SalesChannelQueryController {

    private final SearchSalesChannelByOffsetUseCase searchSalesChannelByOffsetUseCase;
    private final SalesChannelQueryApiMapper mapper;

    public SalesChannelQueryController(
            SearchSalesChannelByOffsetUseCase searchSalesChannelByOffsetUseCase,
            SalesChannelQueryApiMapper mapper) {
        this.searchSalesChannelByOffsetUseCase = searchSalesChannelByOffsetUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "판매채널 목록 조회", description = "판매채널 목록을 복합 조건으로 조회합니다.")
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "sales-channel:read", description = "판매채널 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<SalesChannelApiResponse>>>
            searchSalesChannels(@ParameterObject @Valid SearchSalesChannelsApiRequest request) {

        SalesChannelSearchParams params = mapper.toSearchParams(request);
        SalesChannelPageResult pageResult = searchSalesChannelByOffsetUseCase.execute(params);
        PageApiResponse<SalesChannelApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
