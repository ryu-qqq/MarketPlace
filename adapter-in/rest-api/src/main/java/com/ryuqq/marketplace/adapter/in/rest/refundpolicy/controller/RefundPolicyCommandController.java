package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.RefundPolicyAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.ChangeRefundPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.RegisterRefundPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.UpdateRefundPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.RegisterRefundPolicyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.mapper.RefundPolicyCommandApiMapper;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.ChangeRefundPolicyStatusCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.RegisterRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.UpdateRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.port.in.command.ChangeRefundPolicyStatusUseCase;
import com.ryuqq.marketplace.application.refundpolicy.port.in.command.RegisterRefundPolicyUseCase;
import com.ryuqq.marketplace.application.refundpolicy.port.in.command.UpdateRefundPolicyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RefundPolicyCommandController - 환불정책 생성/수정 API.
 *
 * <p>환불정책 생성 및 수정 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: Controller는 @RestController로 정의.
 *
 * <p>API-CTR-004: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-002: DELETE 메서드 금지 (소프트 삭제는 PATCH).
 *
 * <p>API-CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>API-CTR-007: Controller에 비즈니스 로직 포함 금지.
 *
 * <p>API-CTR-009: @Valid 어노테이션 필수.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "환불정책 관리", description = "환불정책 생성/수정 API")
@RestController
@RequestMapping(RefundPolicyAdminEndpoints.REFUND_POLICIES)
public class RefundPolicyCommandController {

    private final RegisterRefundPolicyUseCase registerUseCase;
    private final UpdateRefundPolicyUseCase updateUseCase;
    private final ChangeRefundPolicyStatusUseCase changeStatusUseCase;
    private final RefundPolicyCommandApiMapper mapper;

    /**
     * RefundPolicyCommandController 생성자.
     *
     * @param registerUseCase 환불정책 등록 UseCase
     * @param updateUseCase 환불정책 수정 UseCase
     * @param changeStatusUseCase 환불정책 상태 변경 UseCase
     * @param mapper Command API 매퍼
     */
    public RefundPolicyCommandController(
            RegisterRefundPolicyUseCase registerUseCase,
            UpdateRefundPolicyUseCase updateUseCase,
            ChangeRefundPolicyStatusUseCase changeStatusUseCase,
            RefundPolicyCommandApiMapper mapper) {
        this.registerUseCase = registerUseCase;
        this.updateUseCase = updateUseCase;
        this.changeStatusUseCase = changeStatusUseCase;
        this.mapper = mapper;
    }

    /**
     * 환불정책 등록 API.
     *
     * <p>새로운 환불정책을 등록합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 등록 요청 DTO
     * @return 생성된 환불정책 ID
     */
    @Operation(summary = "환불정책 등록", description = "새로운 환불정책을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'refund-policy:write')")
    @RequirePermission(value = "refund-policy:write", description = "환불정책 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<RegisterRefundPolicyApiResponse>> register(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(RefundPolicyAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Valid @RequestBody RegisterRefundPolicyApiRequest request) {

        RegisterRefundPolicyCommand command = mapper.toCommand(sellerId, request);
        Long createdId = registerUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(new RegisterRefundPolicyApiResponse(createdId)));
    }

    /**
     * 환불정책 수정 API.
     *
     * <p>기존 환불정책의 정보를 수정합니다.
     *
     * @param sellerId 셀러 ID
     * @param policyId 환불정책 ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "환불정책 수정", description = "환불정책의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "환불정책을 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'refund-policy:write')")
    @RequirePermission(value = "refund-policy:write", description = "환불정책 수정")
    @PutMapping(RefundPolicyAdminEndpoints.ID)
    public ResponseEntity<Void> update(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(RefundPolicyAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Parameter(description = "환불정책 ID", required = true)
                    @PathVariable(RefundPolicyAdminEndpoints.PATH_POLICY_ID)
                    Long policyId,
            @Valid @RequestBody UpdateRefundPolicyApiRequest request) {

        UpdateRefundPolicyCommand command = mapper.toCommand(sellerId, policyId, request);
        updateUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 환불정책 다건 상태 변경 API.
     *
     * <p>선택한 환불정책들의 활성화 상태를 변경합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 상태 변경 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "환불정책 다건 상태 변경", description = "선택한 환불정책들의 활성화 상태를 변경합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'refund-policy:write')")
    @RequirePermission(value = "refund-policy:write", description = "환불정책 상태 변경")
    @PatchMapping(RefundPolicyAdminEndpoints.STATUS)
    public ResponseEntity<Void> changeStatus(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(RefundPolicyAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Valid @RequestBody ChangeRefundPolicyStatusApiRequest request) {

        ChangeRefundPolicyStatusCommand command = mapper.toCommand(sellerId, request);
        changeStatusUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
