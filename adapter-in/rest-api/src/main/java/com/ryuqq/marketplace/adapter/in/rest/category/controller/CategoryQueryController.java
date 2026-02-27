package com.ryuqq.marketplace.adapter.in.rest.category.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ryuqq.marketplace.adapter.in.rest.category.CategoryAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.query.SearchCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.mapper.CategoryQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.category.dto.response.CategoryPageResult;
import com.ryuqq.marketplace.application.category.port.in.query.SearchCategoryByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 카테고리 조회 API 컨트롤러. */
@Tag(name = "카테고리 조회", description = "카테고리 조회 API")
@RestController
@RequestMapping(CategoryAdminEndpoints.CATEGORIES)
public class CategoryQueryController {

    private final SearchCategoryByOffsetUseCase searchCategoryByOffsetUseCase;
    private final CategoryQueryApiMapper mapper;

    public CategoryQueryController(
            SearchCategoryByOffsetUseCase searchCategoryByOffsetUseCase,
            CategoryQueryApiMapper mapper) {
        this.searchCategoryByOffsetUseCase = searchCategoryByOffsetUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "카테고리 목록 조회", description = "카테고리 목록을 조회합니다.")
    @PreAuthorize("@access.hasPermission('category:read')")
    @RequirePermission(value = "category:read", description = "카테고리 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<CategoryApiResponse>>>
            searchCategoriesByOffset(@ParameterObject @Valid SearchCategoriesApiRequest request) {
        CategoryPageResult pageResult =
                searchCategoryByOffsetUseCase.execute(mapper.toSearchParams(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(pageResult)));
    }
}
