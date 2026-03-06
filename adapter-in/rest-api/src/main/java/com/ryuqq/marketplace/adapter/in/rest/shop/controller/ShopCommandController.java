package com.ryuqq.marketplace.adapter.in.rest.shop.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shop.ShopAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.RegisterShopApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.UpdateShopApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.response.ShopIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shop.mapper.ShopCommandApiMapper;
import com.ryuqq.marketplace.application.shop.dto.command.RegisterShopCommand;
import com.ryuqq.marketplace.application.shop.dto.command.UpdateShopCommand;
import com.ryuqq.marketplace.application.shop.port.in.command.RegisterShopUseCase;
import com.ryuqq.marketplace.application.shop.port.in.command.UpdateShopUseCase;
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

/** Shop 커맨드 Controller. */
@Tag(name = "외부몰 커맨드", description = "외부몰 커맨드 API (등록/수정)")
@RestController
@RequestMapping(ShopAdminEndpoints.SHOPS)
public class ShopCommandController {

    private final RegisterShopUseCase registerShopUseCase;
    private final UpdateShopUseCase updateShopUseCase;
    private final ShopCommandApiMapper mapper;

    public ShopCommandController(
            RegisterShopUseCase registerShopUseCase,
            UpdateShopUseCase updateShopUseCase,
            ShopCommandApiMapper mapper) {
        this.registerShopUseCase = registerShopUseCase;
        this.updateShopUseCase = updateShopUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부몰 등록", description = "새로운 외부몰을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.hasPermission('shop:write')")
    @RequirePermission(value = "shop:write", description = "외부몰 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<ShopIdApiResponse>> registerShop(
            @Valid @RequestBody RegisterShopApiRequest request) {

        RegisterShopCommand command = mapper.toCommand(request);
        Long shopId = registerShopUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(ShopIdApiResponse.of(shopId)));
    }

    @Operation(summary = "외부몰 수정", description = "외부몰 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "외부몰을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('shop:write')")
    @RequirePermission(value = "shop:write", description = "외부몰 수정")
    @PutMapping(ShopAdminEndpoints.SHOP_ID)
    public ResponseEntity<Void> updateShop(
            @Parameter(description = "Shop ID", required = true)
                    @PathVariable(ShopAdminEndpoints.PATH_SHOP_ID)
                    Long shopId,
            @Valid @RequestBody UpdateShopApiRequest request) {

        UpdateShopCommand command = mapper.toCommand(shopId, request);
        updateShopUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
