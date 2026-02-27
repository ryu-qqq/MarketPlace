package com.ryuqq.marketplace.adapter.in.rest.categorypreset.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.CategoryPresetAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.DeleteCategoryPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.RegisterCategoryPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.UpdateCategoryPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response.CategoryPresetIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response.DeleteCategoryPresetsApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.mapper.CategoryPresetCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.DeleteCategoryPresetsUseCase;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.RegisterCategoryPresetUseCase;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.UpdateCategoryPresetUseCase;
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

/** 카테고리 프리셋 커맨드 Controller. */
@Tag(name = "카테고리 프리셋 커맨드", description = "카테고리 프리셋 커맨드 API (등록/수정/삭제)")
@RestController
@RequestMapping(CategoryPresetAdminEndpoints.CATEGORY_PRESETS)
public class CategoryPresetCommandController {

    private final RegisterCategoryPresetUseCase registerCategoryPresetUseCase;
    private final UpdateCategoryPresetUseCase updateCategoryPresetUseCase;
    private final DeleteCategoryPresetsUseCase deleteCategoryPresetsUseCase;
    private final CategoryPresetCommandApiMapper mapper;

    public CategoryPresetCommandController(
            RegisterCategoryPresetUseCase registerCategoryPresetUseCase,
            UpdateCategoryPresetUseCase updateCategoryPresetUseCase,
            DeleteCategoryPresetsUseCase deleteCategoryPresetsUseCase,
            CategoryPresetCommandApiMapper mapper) {
        this.registerCategoryPresetUseCase = registerCategoryPresetUseCase;
        this.updateCategoryPresetUseCase = updateCategoryPresetUseCase;
        this.deleteCategoryPresetsUseCase = deleteCategoryPresetsUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "카테고리 프리셋 등록", description = "카테고리 프리셋을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.hasPermission('category-preset:write')")
    @RequirePermission(value = "category-preset:write", description = "카테고리 프리셋 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryPresetIdApiResponse>> registerCategoryPreset(
            @Valid @RequestBody RegisterCategoryPresetApiRequest request) {

        RegisterCategoryPresetCommand command = mapper.toRegisterCommand(request);
        Long categoryPresetId = registerCategoryPresetUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(CategoryPresetIdApiResponse.of(categoryPresetId, null)));
    }

    @Operation(summary = "카테고리 프리셋 수정", description = "카테고리 프리셋을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "프리셋을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('category-preset:write')")
    @RequirePermission(value = "category-preset:write", description = "카테고리 프리셋 수정")
    @PutMapping(CategoryPresetAdminEndpoints.CATEGORY_PRESET_ID)
    public ResponseEntity<Void> updateCategoryPreset(
            @Parameter(description = "프리셋 ID", required = true)
                    @PathVariable(CategoryPresetAdminEndpoints.PATH_CATEGORY_PRESET_ID)
                    Long categoryPresetId,
            @Valid @RequestBody UpdateCategoryPresetApiRequest request) {

        UpdateCategoryPresetCommand command = mapper.toUpdateCommand(categoryPresetId, request);
        updateCategoryPresetUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "카테고리 프리셋 벌크 삭제", description = "선택한 카테고리 프리셋을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "삭제 성공")
    })
    @PreAuthorize("@access.hasPermission('category-preset:write')")
    @RequirePermission(value = "category-preset:write", description = "카테고리 프리셋 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse<DeleteCategoryPresetsApiResponse>> deleteCategoryPresets(
            @Valid @RequestBody DeleteCategoryPresetsApiRequest request) {

        DeleteCategoryPresetsCommand command = mapper.toDeleteCommand(request);
        int deletedCount = deleteCategoryPresetsUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of(DeleteCategoryPresetsApiResponse.of(deletedCount)));
    }
}
