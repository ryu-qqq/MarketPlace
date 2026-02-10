package com.ryuqq.marketplace.adapter.in.rest.brandpreset.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.BrandPresetAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.query.SearchBrandPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.BrandPresetApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.mapper.BrandPresetQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.port.in.query.SearchBrandPresetByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 브랜드 프리셋 조회 Controller. */
@Tag(name = "브랜드 프리셋 조회", description = "브랜드 프리셋 조회 API")
@RestController
@RequestMapping(BrandPresetAdminEndpoints.BRAND_PRESETS)
public class BrandPresetQueryController {

    private final SearchBrandPresetByOffsetUseCase searchBrandPresetByOffsetUseCase;
    private final BrandPresetQueryApiMapper mapper;

    public BrandPresetQueryController(
            SearchBrandPresetByOffsetUseCase searchBrandPresetByOffsetUseCase,
            BrandPresetQueryApiMapper mapper) {
        this.searchBrandPresetByOffsetUseCase = searchBrandPresetByOffsetUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "브랜드 프리셋 목록 조회", description = "브랜드 프리셋 목록을 조회합니다.")
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "brand-preset:read", description = "브랜드 프리셋 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<BrandPresetApiResponse>>> searchBrandPresets(
            @ParameterObject @Valid SearchBrandPresetsApiRequest request) {

        BrandPresetSearchParams params = mapper.toSearchParams(request);
        BrandPresetPageResult pageResult = searchBrandPresetByOffsetUseCase.execute(params);
        PageApiResponse<BrandPresetApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
