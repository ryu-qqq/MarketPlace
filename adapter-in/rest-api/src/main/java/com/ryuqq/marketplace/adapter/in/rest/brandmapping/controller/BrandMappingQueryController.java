package com.ryuqq.marketplace.adapter.in.rest.brandmapping.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.brandmapping.BrandMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.brandmapping.dto.query.SearchBrandMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandmapping.dto.response.BrandMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brandmapping.mapper.BrandMappingQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.brandmapping.dto.query.BrandMappingSearchParams;
import com.ryuqq.marketplace.application.brandmapping.dto.response.BrandMappingPageResult;
import com.ryuqq.marketplace.application.brandmapping.port.in.query.SearchBrandMappingByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 브랜드 매핑 조회 Controller. */
@Tag(name = "브랜드 매핑 조회", description = "브랜드 매핑 조회 API")
@RestController
@RequestMapping(BrandMappingAdminEndpoints.BRAND_MAPPINGS)
public class BrandMappingQueryController {

    private final SearchBrandMappingByOffsetUseCase searchBrandMappingByOffsetUseCase;
    private final BrandMappingQueryApiMapper mapper;

    public BrandMappingQueryController(
            SearchBrandMappingByOffsetUseCase searchBrandMappingByOffsetUseCase,
            BrandMappingQueryApiMapper mapper) {
        this.searchBrandMappingByOffsetUseCase = searchBrandMappingByOffsetUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "브랜드 매핑 목록 조회", description = "브랜드 매핑 목록을 복합 조건으로 조회합니다.")
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "brand-mapping:read", description = "브랜드 매핑 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<BrandMappingApiResponse>>>
            searchBrandMappings(@ParameterObject @Valid SearchBrandMappingsApiRequest request) {

        BrandMappingSearchParams params = mapper.toSearchParams(request);
        BrandMappingPageResult pageResult = searchBrandMappingByOffsetUseCase.execute(params);
        PageApiResponse<BrandMappingApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
