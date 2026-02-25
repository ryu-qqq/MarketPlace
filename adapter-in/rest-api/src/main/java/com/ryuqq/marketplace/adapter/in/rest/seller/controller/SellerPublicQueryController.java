package com.ryuqq.marketplace.adapter.in.rest.seller.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.SellerPublicEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerPublicProfileApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.mapper.SellerQueryApiMapper;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPublicProfileResult;
import com.ryuqq.marketplace.application.seller.port.in.query.GetSellerPublicProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * SellerPublicQueryController - 셀러 공개 조회 API.
 *
 * <p>인증 없이 접근 가능한 셀러 공개 프로필 조회 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-004: ResponseEntity + ApiResponse 래핑.
 *
 * <p>API-CTR-005: Controller @Transactional 금지.
 *
 * <p>API-CTR-007: Controller 비즈니스 로직 금지.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "셀러 공개 조회", description = "셀러 공개 프로필 조회 API")
@RestController
public class SellerPublicQueryController {

    private final GetSellerPublicProfileUseCase getSellerPublicProfileUseCase;
    private final SellerQueryApiMapper mapper;

    /**
     * SellerPublicQueryController 생성자.
     *
     * @param getSellerPublicProfileUseCase 셀러 공개 프로필 조회 UseCase
     * @param mapper Query API 매퍼
     */
    public SellerPublicQueryController(
            GetSellerPublicProfileUseCase getSellerPublicProfileUseCase,
            SellerQueryApiMapper mapper) {
        this.getSellerPublicProfileUseCase = getSellerPublicProfileUseCase;
        this.mapper = mapper;
    }

    /**
     * 셀러 공개 프로필 조회 API.
     *
     * <p>셀러의 공개 프로필(셀러명, 표시명, 회사명, 대표자명)을 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @return 셀러 공개 프로필
     */
    @Operation(summary = "셀러 공개 프로필 조회", description = "셀러의 공개 프로필(셀러명, 표시명, 회사명, 대표자명)을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "셀러를 찾을 수 없음")
    })
    @GetMapping(SellerPublicEndpoints.SELLER_PROFILE)
    public ResponseEntity<ApiResponse<SellerPublicProfileApiResponse>> getSellerPublicProfile(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(SellerPublicEndpoints.PATH_SELLER_ID)
                    Long sellerId) {
        SellerPublicProfileResult result = getSellerPublicProfileUseCase.execute(sellerId);
        SellerPublicProfileApiResponse response = mapper.toPublicProfileResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
