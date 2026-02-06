package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.ShippingPolicyAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.ChangeShippingPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.RegisterShippingPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.UpdateShippingPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response.RegisterShippingPolicyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.mapper.ShippingPolicyCommandApiMapper;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.ChangeShippingPolicyStatusCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.UpdateShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.ChangeShippingPolicyStatusUseCase;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.RegisterShippingPolicyUseCase;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.UpdateShippingPolicyUseCase;
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
 * ShippingPolicyCommandController - 배송정책 생성/수정 API.
 *
 * <p>배송정책 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "배송정책 관리", description = "배송정책 생성/수정 API")
@RestController
@RequestMapping(ShippingPolicyAdminEndpoints.SHIPPING_POLICIES)
public class ShippingPolicyCommandController {

    private final RegisterShippingPolicyUseCase registerUseCase;
    private final UpdateShippingPolicyUseCase updateUseCase;
    private final ChangeShippingPolicyStatusUseCase changeStatusUseCase;
    private final ShippingPolicyCommandApiMapper mapper;

    /**
     * ShippingPolicyCommandController 생성자.
     *
     * @param registerUseCase 배송정책 등록 UseCase
     * @param updateUseCase 배송정책 수정 UseCase
     * @param changeStatusUseCase 배송정책 상태 변경 UseCase
     * @param mapper Command API 매퍼
     */
    public ShippingPolicyCommandController(
            RegisterShippingPolicyUseCase registerUseCase,
            UpdateShippingPolicyUseCase updateUseCase,
            ChangeShippingPolicyStatusUseCase changeStatusUseCase,
            ShippingPolicyCommandApiMapper mapper) {
        this.registerUseCase = registerUseCase;
        this.updateUseCase = updateUseCase;
        this.changeStatusUseCase = changeStatusUseCase;
        this.mapper = mapper;
    }

    /**
     * 배송정책 등록 API.
     *
     * <p>새로운 배송정책을 등록합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 등록 요청 DTO
     * @return 생성된 배송정책 ID
     */
    @Operation(summary = "배송정책 등록", description = "새로운 배송정책을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'shipping-policy:write')")
    @RequirePermission(value = "shipping-policy:write", description = "배송정책 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<RegisterShippingPolicyApiResponse>> register(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(ShippingPolicyAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Valid @RequestBody RegisterShippingPolicyApiRequest request) {

        RegisterShippingPolicyCommand command = mapper.toCommand(sellerId, request);
        Long createdId = registerUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(new RegisterShippingPolicyApiResponse(createdId)));
    }

    /**
     * 배송정책 수정 API.
     *
     * <p>기존 배송정책의 정보를 수정합니다.
     *
     * @param sellerId 셀러 ID
     * @param policyId 배송정책 ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "배송정책 수정", description = "배송정책의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "배송정책을 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'shipping-policy:write')")
    @RequirePermission(value = "shipping-policy:write", description = "배송정책 수정")
    @PutMapping(ShippingPolicyAdminEndpoints.ID)
    public ResponseEntity<Void> update(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(ShippingPolicyAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Parameter(description = "배송정책 ID", required = true)
                    @PathVariable(ShippingPolicyAdminEndpoints.PATH_POLICY_ID)
                    Long policyId,
            @Valid @RequestBody UpdateShippingPolicyApiRequest request) {

        UpdateShippingPolicyCommand command = mapper.toCommand(sellerId, policyId, request);
        updateUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 배송정책 다건 상태 변경 API.
     *
     * <p>선택한 배송정책들의 활성화 상태를 변경합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 상태 변경 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "배송정책 다건 상태 변경", description = "선택한 배송정책들의 활성화 상태를 변경합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'shipping-policy:write')")
    @RequirePermission(value = "shipping-policy:write", description = "배송정책 상태 변경")
    @PatchMapping(ShippingPolicyAdminEndpoints.STATUS)
    public ResponseEntity<Void> changeStatus(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(ShippingPolicyAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Valid @RequestBody ChangeShippingPolicyStatusApiRequest request) {

        ChangeShippingPolicyStatusCommand command = mapper.toCommand(sellerId, request);
        changeStatusUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
