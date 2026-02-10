package com.ryuqq.marketplace.adapter.in.rest.brandmapping.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.brandmapping.BrandMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.brandmapping.dto.command.RegisterBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandmapping.dto.response.BrandMappingIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brandmapping.mapper.BrandMappingCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.brandmapping.dto.command.DeleteBrandMappingCommand;
import com.ryuqq.marketplace.application.brandmapping.dto.command.RegisterBrandMappingCommand;
import com.ryuqq.marketplace.application.brandmapping.port.in.command.DeleteBrandMappingUseCase;
import com.ryuqq.marketplace.application.brandmapping.port.in.command.RegisterBrandMappingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 브랜드 매핑 커맨드 Controller. */
@Tag(name = "브랜드 매핑 커맨드", description = "브랜드 매핑 커맨드 API (등록/삭제)")
@RestController
@RequestMapping(BrandMappingAdminEndpoints.BRAND_MAPPINGS)
public class BrandMappingCommandController {

    private final RegisterBrandMappingUseCase registerBrandMappingUseCase;
    private final DeleteBrandMappingUseCase deleteBrandMappingUseCase;
    private final BrandMappingCommandApiMapper mapper;

    public BrandMappingCommandController(
            RegisterBrandMappingUseCase registerBrandMappingUseCase,
            DeleteBrandMappingUseCase deleteBrandMappingUseCase,
            BrandMappingCommandApiMapper mapper) {
        this.registerBrandMappingUseCase = registerBrandMappingUseCase;
        this.deleteBrandMappingUseCase = deleteBrandMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "브랜드 매핑 등록", description = "외부↔내부 브랜드 매핑을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "이미 매핑이 존재함")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "brand-mapping:write", description = "브랜드 매핑 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<BrandMappingIdApiResponse>> registerBrandMapping(
            @Valid @RequestBody RegisterBrandMappingApiRequest request) {

        RegisterBrandMappingCommand command = mapper.toCommand(request);
        Long brandMappingId = registerBrandMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(BrandMappingIdApiResponse.of(brandMappingId)));
    }

    @Operation(summary = "브랜드 매핑 삭제", description = "브랜드 매핑을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "매핑을 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "brand-mapping:write", description = "브랜드 매핑 삭제")
    @DeleteMapping(BrandMappingAdminEndpoints.BRAND_MAPPING_ID)
    public ResponseEntity<Void> deleteBrandMapping(
            @Parameter(description = "브랜드 매핑 ID", required = true)
                    @PathVariable(BrandMappingAdminEndpoints.PATH_BRAND_MAPPING_ID)
                    Long brandMappingId) {

        deleteBrandMappingUseCase.execute(new DeleteBrandMappingCommand(brandMappingId));

        return ResponseEntity.noContent().build();
    }
}
