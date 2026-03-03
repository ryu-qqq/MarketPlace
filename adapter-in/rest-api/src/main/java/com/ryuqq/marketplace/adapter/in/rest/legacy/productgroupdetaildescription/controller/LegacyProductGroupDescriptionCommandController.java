package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.LegacyProductGroupDescriptionEndpoints.DETAIL_DESCRIPTION;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.mapper.LegacyDescriptionCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.description.port.in.command.LegacyProductUpdateDescriptionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 세토프 어드민용 레거시 상품그룹 상세설명 수정 API 컨트롤러. */
@Tag(name = "세토프 어드민용 레거시 - 상세설명", description = "세토프 어드민용 레거시 상품그룹 상세설명 엔드포인트.")
@RestController
public class LegacyProductGroupDescriptionCommandController {

    private final LegacyProductUpdateDescriptionUseCase legacyProductUpdateDescriptionUseCase;
    private final LegacyDescriptionCommandApiMapper legacyDescriptionCommandApiMapper;

    public LegacyProductGroupDescriptionCommandController(
            LegacyProductUpdateDescriptionUseCase legacyProductUpdateDescriptionUseCase,
            LegacyDescriptionCommandApiMapper legacyDescriptionCommandApiMapper) {
        this.legacyProductUpdateDescriptionUseCase = legacyProductUpdateDescriptionUseCase;
        this.legacyDescriptionCommandApiMapper = legacyDescriptionCommandApiMapper;
    }

    @Operation(summary = "레거시 상품그룹 상세설명 수정", description = "세토프 어드민용 레거시 상품그룹의 상세설명을 수정합니다.")
    @PreAuthorize("@access.isLegacyProductOwnerOrSuperAdmin(#productGroupId)")
    @RequirePermission(value = "legacy:product-group:write", description = "레거시 상품그룹 상세설명 수정")
    @PutMapping(DETAIL_DESCRIPTION)
    public ResponseEntity<LegacyApiResponse<Long>> updateDetailDescription(
            @PathVariable long productGroupId,
            @Valid @RequestBody LegacyUpdateProductDescriptionRequest request) {
        legacyProductUpdateDescriptionUseCase.execute(
                legacyDescriptionCommandApiMapper.toLegacyUpdateDescriptionCommand(
                        productGroupId, request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }
}
