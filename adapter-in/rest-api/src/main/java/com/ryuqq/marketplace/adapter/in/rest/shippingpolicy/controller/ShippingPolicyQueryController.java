package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.ShippingPolicyAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.query.SearchShippingPoliciesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response.ShippingPolicyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.mapper.ShippingPolicyQueryApiMapper;
import com.ryuqq.marketplace.application.shippingpolicy.dto.query.ShippingPolicySearchParams;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.query.SearchShippingPolicyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ShippingPolicyQueryController - 배송정책 조회 API.
 *
 * <p>배송정책 조회 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * <p>API-CTR-012: URL 경로 소문자 + 복수형 (/shipping-policies).
 *
 * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "배송정책 조회", description = "배송정책 조회 API")
@RestController
@RequestMapping(ShippingPolicyAdminEndpoints.SHIPPING_POLICIES)
public class ShippingPolicyQueryController {

    private final SearchShippingPolicyUseCase searchShippingPolicyUseCase;
    private final ShippingPolicyQueryApiMapper mapper;

    /**
     * ShippingPolicyQueryController 생성자.
     *
     * @param searchShippingPolicyUseCase 배송정책 검색 UseCase
     * @param mapper Query API 매퍼
     */
    public ShippingPolicyQueryController(
            SearchShippingPolicyUseCase searchShippingPolicyUseCase,
            ShippingPolicyQueryApiMapper mapper) {
        this.searchShippingPolicyUseCase = searchShippingPolicyUseCase;
        this.mapper = mapper;
    }

    /**
     * 배송정책 페이지 조회 API.
     *
     * <p>배송정책 목록을 페이지 기반으로 조회합니다.
     *
     * <p>API-CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param sellerId 셀러 ID
     * @param request 조회 요청 DTO (페이지 기반)
     * @return 배송정책 페이지 목록
     */
    @Operation(summary = "배송정책 목록 조회", description = "배송정책 목록을 페이지 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'shipping-policy:read')")
    @RequirePermission(value = "shipping-policy:read", description = "배송정책 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ShippingPolicyApiResponse>>> search(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(ShippingPolicyAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Valid SearchShippingPoliciesPageApiRequest request) {

        ShippingPolicySearchParams searchParams = mapper.toSearchParams(sellerId, request);
        ShippingPolicyPageResult pageResult = searchShippingPolicyUseCase.execute(searchParams);
        PageApiResponse<ShippingPolicyApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
