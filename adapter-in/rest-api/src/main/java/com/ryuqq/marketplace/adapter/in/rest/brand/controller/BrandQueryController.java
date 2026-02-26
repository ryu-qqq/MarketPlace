package com.ryuqq.marketplace.adapter.in.rest.brand.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.brand.BrandAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.query.SearchBrandsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.mapper.BrandQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.port.in.query.SearchBrandByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 브랜드 조회 API 컨트롤러. */
@Tag(name = "브랜드 조회", description = "브랜드 조회 API")
@RestController
@RequestMapping(BrandAdminEndpoints.BRANDS)
public class BrandQueryController {

    private final SearchBrandByOffsetUseCase searchBrandByOffsetUseCase;
    private final BrandQueryApiMapper mapper;

    public BrandQueryController(
            SearchBrandByOffsetUseCase searchBrandByOffsetUseCase, BrandQueryApiMapper mapper) {
        this.searchBrandByOffsetUseCase = searchBrandByOffsetUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "브랜드 목록 조회", description = "브랜드 목록을 조회합니다.")
    @RequirePermission(value = "brand:read", description = "브랜드 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<BrandApiResponse>>> searchBrandsByOffset(
            @ParameterObject @Valid SearchBrandsApiRequest request) {
        BrandPageResult pageResult =
                searchBrandByOffsetUseCase.execute(mapper.toSearchParams(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(pageResult)));
    }
}
