package com.ryuqq.marketplace.adapter.in.rest.legacy.notice.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.notice.LegacyNoticeEndpoints.NOTICE;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.notice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.notice.mapper.LegacyNoticeCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.notice.port.in.command.LegacyProductUpdateNoticeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 세토프 어드민용 레거시 고시정보 수정 API 컨트롤러. */
@Tag(name = "세토프 어드민용 레거시 - 고시정보", description = "세토프 어드민용 레거시 고시정보 엔드포인트.")
@RestController
public class LegacyNoticeCommandController {

    private final LegacyProductUpdateNoticeUseCase legacyProductUpdateNoticeUseCase;
    private final LegacyNoticeCommandApiMapper legacyNoticeCommandApiMapper;

    public LegacyNoticeCommandController(
            LegacyProductUpdateNoticeUseCase legacyProductUpdateNoticeUseCase,
            LegacyNoticeCommandApiMapper legacyNoticeCommandApiMapper) {
        this.legacyProductUpdateNoticeUseCase = legacyProductUpdateNoticeUseCase;
        this.legacyNoticeCommandApiMapper = legacyNoticeCommandApiMapper;
    }

    @Operation(summary = "레거시 고시정보 수정", description = "세토프 어드민용 레거시 상품그룹의 고시정보를 수정합니다.")
    @PreAuthorize("@access.isLegacyProductOwnerOrSuperAdmin(#productGroupId)")
    @RequirePermission(value = "legacy:product-group:write", description = "레거시 고시정보 수정")
    @PutMapping(NOTICE)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductNotice(
            @PathVariable long productGroupId,
            @Valid @RequestBody LegacyCreateProductNoticeRequest request) {
        legacyProductUpdateNoticeUseCase.execute(
                legacyNoticeCommandApiMapper.toLegacyNoticeCommand(productGroupId, request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }
}
