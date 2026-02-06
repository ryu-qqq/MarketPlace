package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.RefundPolicyAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.query.SearchRefundPoliciesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.RefundPolicyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.mapper.RefundPolicyQueryApiMapper;
import com.ryuqq.marketplace.application.refundpolicy.dto.query.RefundPolicySearchParams;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;
import com.ryuqq.marketplace.application.refundpolicy.port.in.query.SearchRefundPolicyUseCase;
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
 * RefundPolicyQueryController - 환불정책 조회 API.
 *
 * <p>환불정책 조회 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * <p>API-CTR-012: URL 경로 소문자 + 복수형 (/refund-policies).
 *
 * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "환불정책 조회", description = "환불정책 조회 API")
@RestController
@RequestMapping(RefundPolicyAdminEndpoints.REFUND_POLICIES)
public class RefundPolicyQueryController {

    private final SearchRefundPolicyUseCase searchRefundPolicyUseCase;
    private final RefundPolicyQueryApiMapper mapper;

    /**
     * RefundPolicyQueryController 생성자.
     *
     * @param searchRefundPolicyUseCase 환불정책 검색 UseCase
     * @param mapper Query API 매퍼
     */
    public RefundPolicyQueryController(
            SearchRefundPolicyUseCase searchRefundPolicyUseCase,
            RefundPolicyQueryApiMapper mapper) {
        this.searchRefundPolicyUseCase = searchRefundPolicyUseCase;
        this.mapper = mapper;
    }

    /**
     * 환불정책 페이지 조회 API.
     *
     * <p>환불정책 목록을 페이지 기반으로 조회합니다.
     *
     * <p>API-CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param sellerId 셀러 ID
     * @param request 조회 요청 DTO (페이지 기반)
     * @return 환불정책 페이지 목록
     */
    @Operation(summary = "환불정책 목록 조회", description = "환불정책 목록을 페이지 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'refund-policy:read')")
    @RequirePermission(value = "refund-policy:read", description = "환불정책 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<RefundPolicyApiResponse>>> search(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(RefundPolicyAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Valid SearchRefundPoliciesPageApiRequest request) {

        RefundPolicySearchParams searchParams = mapper.toSearchParams(sellerId, request);
        RefundPolicyPageResult pageResult = searchRefundPolicyUseCase.execute(searchParams);
        PageApiResponse<RefundPolicyApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
