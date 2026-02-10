package com.ryuqq.marketplace.adapter.in.rest.brandpreset.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.BrandPresetAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.DeleteBrandPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.RegisterBrandPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.UpdateBrandPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.BrandPresetIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.DeleteBrandPresetsApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.mapper.BrandPresetCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.DeleteBrandPresetsUseCase;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.RegisterBrandPresetUseCase;
import com.ryuqq.marketplace.application.brandpreset.port.in.command.UpdateBrandPresetUseCase;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 브랜드 프리셋 커맨드 Controller. */
@Tag(name = "브랜드 프리셋 커맨드", description = "브랜드 프리셋 커맨드 API (등록/수정/삭제)")
@RestController
@RequestMapping(BrandPresetAdminEndpoints.BRAND_PRESETS)
public class BrandPresetCommandController {

    private final RegisterBrandPresetUseCase registerBrandPresetUseCase;
    private final UpdateBrandPresetUseCase updateBrandPresetUseCase;
    private final DeleteBrandPresetsUseCase deleteBrandPresetsUseCase;
    private final BrandPresetCommandApiMapper mapper;

    public BrandPresetCommandController(
            RegisterBrandPresetUseCase registerBrandPresetUseCase,
            UpdateBrandPresetUseCase updateBrandPresetUseCase,
            DeleteBrandPresetsUseCase deleteBrandPresetsUseCase,
            BrandPresetCommandApiMapper mapper) {
        this.registerBrandPresetUseCase = registerBrandPresetUseCase;
        this.updateBrandPresetUseCase = updateBrandPresetUseCase;
        this.deleteBrandPresetsUseCase = deleteBrandPresetsUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "브랜드 프리셋 등록", description = "브랜드 프리셋을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "brand-preset:write", description = "브랜드 프리셋 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<BrandPresetIdApiResponse>> registerBrandPreset(
            @Valid @RequestBody RegisterBrandPresetApiRequest request) {

        RegisterBrandPresetCommand command = mapper.toCommand(request);
        Long brandPresetId = registerBrandPresetUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(BrandPresetIdApiResponse.of(brandPresetId, null)));
    }

    @Operation(summary = "브랜드 프리셋 수정", description = "브랜드 프리셋을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "프리셋을 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "brand-preset:write", description = "브랜드 프리셋 수정")
    @PutMapping(BrandPresetAdminEndpoints.BRAND_PRESET_ID)
    public ResponseEntity<Void> updateBrandPreset(
            @Parameter(description = "프리셋 ID", required = true)
                    @PathVariable(BrandPresetAdminEndpoints.PATH_BRAND_PRESET_ID)
                    Long brandPresetId,
            @Valid @RequestBody UpdateBrandPresetApiRequest request) {

        UpdateBrandPresetCommand command = mapper.toCommand(brandPresetId, request);
        updateBrandPresetUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "브랜드 프리셋 벌크 삭제", description = "선택한 브랜드 프리셋을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "삭제 성공")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "brand-preset:write", description = "브랜드 프리셋 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse<DeleteBrandPresetsApiResponse>> deleteBrandPresets(
            @Valid @RequestBody DeleteBrandPresetsApiRequest request) {

        DeleteBrandPresetsCommand command = mapper.toDeleteCommand(request.ids());
        int deletedCount = deleteBrandPresetsUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of(DeleteBrandPresetsApiResponse.of(deletedCount)));
    }
}
