package com.ryuqq.marketplace.adapter.in.rest.categorymapping.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.categorymapping.CategoryMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.command.RegisterCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.response.CategoryMappingIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.categorymapping.mapper.CategoryMappingCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.categorymapping.dto.command.DeleteCategoryMappingCommand;
import com.ryuqq.marketplace.application.categorymapping.dto.command.RegisterCategoryMappingCommand;
import com.ryuqq.marketplace.application.categorymapping.port.in.command.DeleteCategoryMappingUseCase;
import com.ryuqq.marketplace.application.categorymapping.port.in.command.RegisterCategoryMappingUseCase;
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

/** 카테고리 매핑 커맨드 Controller. */
@Tag(name = "카테고리 매핑 커맨드", description = "카테고리 매핑 커맨드 API (등록/삭제)")
@RestController
@RequestMapping(CategoryMappingAdminEndpoints.CATEGORY_MAPPINGS)
public class CategoryMappingCommandController {

    private final RegisterCategoryMappingUseCase registerCategoryMappingUseCase;
    private final DeleteCategoryMappingUseCase deleteCategoryMappingUseCase;
    private final CategoryMappingCommandApiMapper mapper;

    public CategoryMappingCommandController(
            RegisterCategoryMappingUseCase registerCategoryMappingUseCase,
            DeleteCategoryMappingUseCase deleteCategoryMappingUseCase,
            CategoryMappingCommandApiMapper mapper) {
        this.registerCategoryMappingUseCase = registerCategoryMappingUseCase;
        this.deleteCategoryMappingUseCase = deleteCategoryMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "카테고리 매핑 등록", description = "외부↔내부 카테고리 매핑을 등록합니다.")
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
    @RequirePermission(value = "category-mapping:write", description = "카테고리 매핑 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryMappingIdApiResponse>> registerCategoryMapping(
            @Valid @RequestBody RegisterCategoryMappingApiRequest request) {

        RegisterCategoryMappingCommand command = mapper.toCommand(request);
        Long categoryMappingId = registerCategoryMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(CategoryMappingIdApiResponse.of(categoryMappingId)));
    }

    @Operation(summary = "카테고리 매핑 삭제", description = "카테고리 매핑을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "매핑을 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "category-mapping:write", description = "카테고리 매핑 삭제")
    @DeleteMapping(CategoryMappingAdminEndpoints.CATEGORY_MAPPING_ID)
    public ResponseEntity<Void> deleteCategoryMapping(
            @Parameter(description = "카테고리 매핑 ID", required = true)
                    @PathVariable(CategoryMappingAdminEndpoints.PATH_CATEGORY_MAPPING_ID)
                    Long categoryMappingId) {

        deleteCategoryMappingUseCase.execute(new DeleteCategoryMappingCommand(categoryMappingId));

        return ResponseEntity.noContent().build();
    }
}
