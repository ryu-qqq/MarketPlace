package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.seller.LegacySellerEndpoints.SELLER;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAuthContextHolder;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.mapper.LegacySellerQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacySellerAuthCompositeReadManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 셀러 API 호환 컨트롤러.
 *
 * <p>GET /seller — 현재 인증된 관리자의 인증 정보를 반환합니다 (세토프 레거시 어드민과 동일 응답).
 */
@Tag(name = "세토프 어드민용 레거시", description = "세토프 어드민 호환 레거시 엔드포인트")
@RestController
public class LegacySellerController {

    private final LegacySellerAuthCompositeReadManager sellerAuthReadManager;
    private final LegacySellerQueryApiMapper mapper;

    public LegacySellerController(
            LegacySellerAuthCompositeReadManager sellerAuthReadManager,
            LegacySellerQueryApiMapper mapper) {
        this.sellerAuthReadManager = sellerAuthReadManager;
        this.mapper = mapper;
    }

    @Operation(summary = "현재 셀러 정보 조회", description = "인증된 사용자의 셀러 인증 정보를 세토프 어드민 호환 형식으로 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping(SELLER)
    public ResponseEntity<LegacyApiResponse<LegacySellerResponse>> getCurrentSeller() {
        String email = LegacyAuthContextHolder.getEmail();
        LegacySellerAuthResult result = sellerAuthReadManager.getByEmail(email);
        return ResponseEntity.ok(LegacyApiResponse.success(mapper.toSellerResponse(result)));
    }
}
