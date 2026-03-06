package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.SalesChannelCategoryAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.command.RegisterSalesChannelCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.response.SalesChannelCategoryIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.mapper.SalesChannelCategoryCommandApiMapper;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.command.RegisterSalesChannelCategoryCommand;
import com.ryuqq.marketplace.application.saleschannelcategory.port.in.command.RegisterSalesChannelCategoryUseCase;
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

/** 외부채널 카테고리 커맨드 Controller. */
@Tag(name = "외부채널 카테고리 커맨드", description = "외부채널 카테고리 커맨드 API (등록)")
@RestController
@RequestMapping(SalesChannelCategoryAdminEndpoints.CATEGORIES)
public class SalesChannelCategoryCommandController {

    private final RegisterSalesChannelCategoryUseCase registerUseCase;
    private final SalesChannelCategoryCommandApiMapper mapper;

    public SalesChannelCategoryCommandController(
            RegisterSalesChannelCategoryUseCase registerUseCase,
            SalesChannelCategoryCommandApiMapper mapper) {
        this.registerUseCase = registerUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부채널 카테고리 등록", description = "외부채널 카테고리를 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.hasPermission('sales-channel-category:write')")
    @RequirePermission(value = "sales-channel-category:write", description = "외부채널 카테고리 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<SalesChannelCategoryIdApiResponse>> registerCategory(
            @Parameter(description = "판매채널 ID", required = true)
                    @PathVariable(SalesChannelCategoryAdminEndpoints.PATH_SALES_CHANNEL_ID)
                    Long salesChannelId,
            @Valid @RequestBody RegisterSalesChannelCategoryApiRequest request) {

        RegisterSalesChannelCategoryCommand command = mapper.toCommand(salesChannelId, request);
        Long categoryId = registerUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(SalesChannelCategoryIdApiResponse.of(categoryId)));
    }
}
