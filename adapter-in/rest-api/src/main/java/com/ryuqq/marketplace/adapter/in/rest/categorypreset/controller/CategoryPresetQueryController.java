package com.ryuqq.marketplace.adapter.in.rest.categorypreset.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.CategoryPresetAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.query.SearchCategoryPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response.CategoryPresetApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response.CategoryPresetDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.mapper.CategoryPresetQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.categorypreset.dto.query.CategoryPresetSearchParams;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;
import com.ryuqq.marketplace.application.categorypreset.port.in.query.GetCategoryPresetDetailUseCase;
import com.ryuqq.marketplace.application.categorypreset.port.in.query.SearchCategoryPresetByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 카테고리 프리셋 조회 Controller. */
@Tag(name = "카테고리 프리셋 조회", description = "카테고리 프리셋 조회 API")
@RestController
@RequestMapping(CategoryPresetAdminEndpoints.CATEGORY_PRESETS)
public class CategoryPresetQueryController {

    private final SearchCategoryPresetByOffsetUseCase searchCategoryPresetByOffsetUseCase;
    private final GetCategoryPresetDetailUseCase getCategoryPresetDetailUseCase;
    private final CategoryPresetQueryApiMapper mapper;

    public CategoryPresetQueryController(
            SearchCategoryPresetByOffsetUseCase searchCategoryPresetByOffsetUseCase,
            GetCategoryPresetDetailUseCase getCategoryPresetDetailUseCase,
            CategoryPresetQueryApiMapper mapper) {
        this.searchCategoryPresetByOffsetUseCase = searchCategoryPresetByOffsetUseCase;
        this.getCategoryPresetDetailUseCase = getCategoryPresetDetailUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "카테고리 프리셋 목록 조회", description = "카테고리 프리셋 목록을 조회합니다.")
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "category-preset:read", description = "카테고리 프리셋 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<CategoryPresetApiResponse>>>
            searchCategoryPresets(@ParameterObject @Valid SearchCategoryPresetsApiRequest request) {

        CategoryPresetSearchParams params = mapper.toSearchParams(request);
        CategoryPresetPageResult pageResult = searchCategoryPresetByOffsetUseCase.execute(params);
        PageApiResponse<CategoryPresetApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "카테고리 프리셋 상세 조회", description = "카테고리 프리셋 상세 정보를 조회합니다.")
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "category-preset:read", description = "카테고리 프리셋 조회")
    @GetMapping(CategoryPresetAdminEndpoints.CATEGORY_PRESET_ID)
    public ResponseEntity<ApiResponse<CategoryPresetDetailApiResponse>> getCategoryPreset(
            @PathVariable(CategoryPresetAdminEndpoints.PATH_CATEGORY_PRESET_ID)
                    Long categoryPresetId) {

        CategoryPresetDetailResult result =
                getCategoryPresetDetailUseCase.execute(categoryPresetId);
        CategoryPresetDetailApiResponse response = mapper.toDetailResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
