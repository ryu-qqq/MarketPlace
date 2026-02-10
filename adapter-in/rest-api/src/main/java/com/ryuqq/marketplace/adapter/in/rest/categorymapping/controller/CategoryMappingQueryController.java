package com.ryuqq.marketplace.adapter.in.rest.categorymapping.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.categorymapping.CategoryMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.query.SearchCategoryMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.response.CategoryMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.categorymapping.mapper.CategoryMappingQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.categorymapping.dto.query.CategoryMappingSearchParams;
import com.ryuqq.marketplace.application.categorymapping.dto.response.CategoryMappingPageResult;
import com.ryuqq.marketplace.application.categorymapping.port.in.query.SearchCategoryMappingByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 카테고리 매핑 조회 Controller. */
@Tag(name = "카테고리 매핑 조회", description = "카테고리 매핑 조회 API")
@RestController
@RequestMapping(CategoryMappingAdminEndpoints.CATEGORY_MAPPINGS)
public class CategoryMappingQueryController {

    private final SearchCategoryMappingByOffsetUseCase searchCategoryMappingByOffsetUseCase;
    private final CategoryMappingQueryApiMapper mapper;

    public CategoryMappingQueryController(
            SearchCategoryMappingByOffsetUseCase searchCategoryMappingByOffsetUseCase,
            CategoryMappingQueryApiMapper mapper) {
        this.searchCategoryMappingByOffsetUseCase = searchCategoryMappingByOffsetUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "카테고리 매핑 목록 조회", description = "카테고리 매핑 목록을 복합 조건으로 조회합니다.")
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "category-mapping:read", description = "카테고리 매핑 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<CategoryMappingApiResponse>>>
            searchCategoryMappings(
                    @ParameterObject @Valid SearchCategoryMappingsApiRequest request) {

        CategoryMappingSearchParams params = mapper.toSearchParams(request);
        CategoryMappingPageResult pageResult = searchCategoryMappingByOffsetUseCase.execute(params);
        PageApiResponse<CategoryMappingApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
