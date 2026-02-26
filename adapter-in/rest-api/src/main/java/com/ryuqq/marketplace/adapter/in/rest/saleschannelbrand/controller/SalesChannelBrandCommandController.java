package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.SalesChannelBrandAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.command.RegisterSalesChannelBrandApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.response.SalesChannelBrandIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.mapper.SalesChannelBrandCommandApiMapper;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.command.RegisterSalesChannelBrandCommand;
import com.ryuqq.marketplace.application.saleschannelbrand.port.in.command.RegisterSalesChannelBrandUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 외부채널 브랜드 커맨드 Controller. */
@Tag(name = "외부채널 브랜드 커맨드", description = "외부채널 브랜드 커맨드 API (등록)")
@RestController
@RequestMapping(SalesChannelBrandAdminEndpoints.BRANDS)
public class SalesChannelBrandCommandController {

    private final RegisterSalesChannelBrandUseCase registerUseCase;
    private final SalesChannelBrandCommandApiMapper mapper;

    public SalesChannelBrandCommandController(
            RegisterSalesChannelBrandUseCase registerUseCase,
            SalesChannelBrandCommandApiMapper mapper) {
        this.registerUseCase = registerUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부채널 브랜드 등록", description = "외부채널 브랜드를 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "sales-channel-brand:write", description = "외부채널 브랜드 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<SalesChannelBrandIdApiResponse>> registerBrand(
            @Parameter(description = "판매채널 ID", required = true)
                    @PathVariable(SalesChannelBrandAdminEndpoints.PATH_SALES_CHANNEL_ID)
                    Long salesChannelId,
            @Valid @RequestBody RegisterSalesChannelBrandApiRequest request) {

        RegisterSalesChannelBrandCommand command = mapper.toCommand(salesChannelId, request);
        Long brandId = registerUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(SalesChannelBrandIdApiResponse.of(brandId)));
    }
}
