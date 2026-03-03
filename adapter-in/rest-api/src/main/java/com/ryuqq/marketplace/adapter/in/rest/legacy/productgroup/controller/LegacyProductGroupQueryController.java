package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupEndpoints.PRODUCT_GROUP_ID;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper.LegacyProductGroupQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 상품그룹 조회 API 컨트롤러.
 *
 * <p>API-CTR-010: CQRS Controller 분리 (Query 전용).
 */
@Tag(name = "세토프 어드민용 레거시 - 상품그룹", description = "세토프 어드민용 레거시 상품그룹 엔드포인트.")
@RestController
public class LegacyProductGroupQueryController {

    private final LegacyProductQueryUseCase legacyProductQueryUseCase;
    private final LegacyProductGroupQueryApiMapper legacyProductGroupQueryApiMapper;

    public LegacyProductGroupQueryController(
            LegacyProductQueryUseCase legacyProductQueryUseCase,
            LegacyProductGroupQueryApiMapper legacyProductGroupQueryApiMapper) {
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
        this.legacyProductGroupQueryApiMapper = legacyProductGroupQueryApiMapper;
    }

    @Operation(summary = "레거시 상품그룹 상세 조회", description = "세토프 어드민 호환 형식으로 상품그룹 상세를 조회합니다.")
    @PreAuthorize("@access.isLegacyProductOwnerOrSuperAdmin(#productGroupId)")
    @RequirePermission(value = "legacy:product-group:read", description = "레거시 상품그룹 상세 조회")
    @GetMapping(PRODUCT_GROUP_ID)
    public ResponseEntity<LegacyApiResponse<LegacyProductDetailApiResponse>> fetchProductGroup(
            @Parameter(description = "조회할 상품그룹 ID") @PathVariable long productGroupId) {
        LegacyProductGroupDetailResult result = legacyProductQueryUseCase.execute(productGroupId);
        LegacyProductDetailApiResponse response =
                legacyProductGroupQueryApiMapper.toResponse(result);
        return ResponseEntity.ok(LegacyApiResponse.of(response));
    }
}
