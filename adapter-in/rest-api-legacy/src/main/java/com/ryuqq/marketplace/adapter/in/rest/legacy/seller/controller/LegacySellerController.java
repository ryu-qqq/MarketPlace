package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.seller.LegacySellerEndpoints.SELLER;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAuthContextHolder;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.mapper.LegacySellerQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.seller.port.in.LegacyGetCurrentSellerUseCase;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 셀러 API 호환 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다. OMS(사방넷, 셀릭)가 호출하는 GET /seller (현재 인증된 셀러 정보 조회)를 제공합니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacySellerController {

    private final LegacyGetCurrentSellerUseCase legacyGetCurrentSellerUseCase;
    private final LegacySellerQueryApiMapper legacySellerQueryApiMapper;

    public LegacySellerController(
            LegacyGetCurrentSellerUseCase legacyGetCurrentSellerUseCase,
            LegacySellerQueryApiMapper legacySellerQueryApiMapper) {
        this.legacyGetCurrentSellerUseCase = legacyGetCurrentSellerUseCase;
        this.legacySellerQueryApiMapper = legacySellerQueryApiMapper;
    }

    @Operation(summary = "현재 셀러 정보 조회", description = "인증된 사용자의 셀러 정보를 세토프 어드민 호환 형식으로 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping(SELLER)
    public ResponseEntity<LegacyApiResponse<LegacySellerResponse>> getCurrentSeller() {
        long sellerId = LegacyAuthContextHolder.getSellerId();
        SellerAdminCompositeResult result = legacyGetCurrentSellerUseCase.execute(sellerId);
        return ResponseEntity.ok(
                LegacyApiResponse.success(legacySellerQueryApiMapper.toSellerResponse(result)));
    }
}
