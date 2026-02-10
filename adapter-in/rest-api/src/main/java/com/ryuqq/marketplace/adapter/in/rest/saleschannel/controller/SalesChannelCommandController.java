package com.ryuqq.marketplace.adapter.in.rest.saleschannel.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.SalesChannelAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.RegisterSalesChannelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.UpdateSalesChannelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.response.SalesChannelIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.mapper.SalesChannelCommandApiMapper;
import com.ryuqq.marketplace.application.saleschannel.dto.command.RegisterSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.dto.command.UpdateSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.port.in.command.RegisterSalesChannelUseCase;
import com.ryuqq.marketplace.application.saleschannel.port.in.command.UpdateSalesChannelUseCase;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 판매채널 커맨드 Controller. */
@Tag(name = "판매채널 커맨드", description = "판매채널 커맨드 API (등록/수정)")
@RestController
@RequestMapping(SalesChannelAdminEndpoints.SALES_CHANNELS)
public class SalesChannelCommandController {

    private final RegisterSalesChannelUseCase registerSalesChannelUseCase;
    private final UpdateSalesChannelUseCase updateSalesChannelUseCase;
    private final SalesChannelCommandApiMapper mapper;

    public SalesChannelCommandController(
            RegisterSalesChannelUseCase registerSalesChannelUseCase,
            UpdateSalesChannelUseCase updateSalesChannelUseCase,
            SalesChannelCommandApiMapper mapper) {
        this.registerSalesChannelUseCase = registerSalesChannelUseCase;
        this.updateSalesChannelUseCase = updateSalesChannelUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "판매채널 등록", description = "새로운 판매채널을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "sales-channel:write", description = "판매채널 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<SalesChannelIdApiResponse>> registerSalesChannel(
            @Valid @RequestBody RegisterSalesChannelApiRequest request) {

        RegisterSalesChannelCommand command = mapper.toCommand(request);
        Long salesChannelId = registerSalesChannelUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(SalesChannelIdApiResponse.of(salesChannelId)));
    }

    @Operation(summary = "판매채널 수정", description = "판매채널 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "판매채널을 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "sales-channel:write", description = "판매채널 수정")
    @PutMapping(SalesChannelAdminEndpoints.SALES_CHANNEL_ID)
    public ResponseEntity<Void> updateSalesChannel(
            @Parameter(description = "판매채널 ID", required = true)
                    @PathVariable(SalesChannelAdminEndpoints.PATH_SALES_CHANNEL_ID)
                    Long salesChannelId,
            @Valid @RequestBody UpdateSalesChannelApiRequest request) {

        UpdateSalesChannelCommand command = mapper.toCommand(salesChannelId, request);
        updateSalesChannelUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
