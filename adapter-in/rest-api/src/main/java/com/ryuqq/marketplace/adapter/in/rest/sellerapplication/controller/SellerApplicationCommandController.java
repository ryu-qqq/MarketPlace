package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.SellerApplicationAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.ApplySellerApplicationApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.RejectSellerApplicationApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response.ApplySellerApplicationApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response.ApproveSellerApplicationApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.mapper.SellerApplicationCommandApiMapper;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApplySellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApproveSellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.RejectSellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.port.in.command.ApplySellerApplicationUseCase;
import com.ryuqq.marketplace.application.sellerapplication.port.in.command.ApproveSellerApplicationUseCase;
import com.ryuqq.marketplace.application.sellerapplication.port.in.command.RejectSellerApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SellerApplicationCommandController - 셀러 입점 신청 Command API.
 *
 * <p>셀러 입점 신청, 승인, 거절 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: Controller는 @RestController로 정의.
 *
 * <p>API-CTR-004: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
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
@Tag(name = "셀러 입점 신청 관리", description = "셀러 입점 신청/승인/거절 API")
@RestController
@RequestMapping(SellerApplicationAdminEndpoints.SELLER_APPLICATIONS)
public class SellerApplicationCommandController {

    private final ApplySellerApplicationUseCase applyUseCase;
    private final ApproveSellerApplicationUseCase approveUseCase;
    private final RejectSellerApplicationUseCase rejectUseCase;
    private final SellerApplicationCommandApiMapper mapper;

    /**
     * SellerApplicationCommandController 생성자.
     *
     * @param applyUseCase 입점 신청 UseCase
     * @param approveUseCase 승인 UseCase
     * @param rejectUseCase 거절 UseCase
     * @param mapper Command API 매퍼
     */
    public SellerApplicationCommandController(
            ApplySellerApplicationUseCase applyUseCase,
            ApproveSellerApplicationUseCase approveUseCase,
            RejectSellerApplicationUseCase rejectUseCase,
            SellerApplicationCommandApiMapper mapper) {
        this.applyUseCase = applyUseCase;
        this.approveUseCase = approveUseCase;
        this.rejectUseCase = rejectUseCase;
        this.mapper = mapper;
    }

    /**
     * 셀러 입점 신청 API.
     *
     * <p>새로운 셀러 입점을 신청합니다.
     *
     * @param request 입점 신청 요청 DTO
     * @return 생성된 신청 ID
     */
    @Operation(summary = "셀러 입점 신청", description = "새로운 셀러 입점을 신청합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "신청 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "seller-application:write", description = "셀러 입점 신청")
    @PostMapping
    public ResponseEntity<ApiResponse<ApplySellerApplicationApiResponse>> apply(
            @Valid @RequestBody ApplySellerApplicationApiRequest request) {

        ApplySellerApplicationCommand command = mapper.toCommand(request);
        Long applicationId = applyUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(new ApplySellerApplicationApiResponse(applicationId)));
    }

    /**
     * 셀러 입점 신청 승인 API.
     *
     * <p>대기 중인 입점 신청을 승인하고 셀러를 생성합니다.
     *
     * @param applicationId 신청 ID
     * @return 생성된 셀러 ID
     */
    @Operation(summary = "셀러 입점 신청 승인", description = "대기 중인 입점 신청을 승인하고 셀러를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "승인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "신청을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "이미 처리된 신청")
    })
    @PreAuthorize("@access.hasPermission('seller-application:approve')")
    @RequirePermission(value = "seller-application:approve", description = "셀러 입점 신청 승인")
    @PostMapping(SellerApplicationAdminEndpoints.APPROVE)
    public ResponseEntity<ApiResponse<ApproveSellerApplicationApiResponse>> approve(
            @Parameter(description = "신청 ID", required = true)
                    @PathVariable(SellerApplicationAdminEndpoints.PATH_APPLICATION_ID)
                    Long applicationId) {

        ApproveSellerApplicationCommand command = mapper.toApproveCommand(applicationId);
        Long sellerId = approveUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of(new ApproveSellerApplicationApiResponse(sellerId)));
    }

    /**
     * 셀러 입점 신청 거절 API.
     *
     * <p>대기 중인 입점 신청을 거절합니다.
     *
     * @param applicationId 신청 ID
     * @param request 거절 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "셀러 입점 신청 거절", description = "대기 중인 입점 신청을 거절합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "거절 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "신청을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "이미 처리된 신청")
    })
    @PreAuthorize("@access.hasPermission('seller-application:reject')")
    @RequirePermission(value = "seller-application:reject", description = "셀러 입점 신청 거절")
    @PostMapping(SellerApplicationAdminEndpoints.REJECT)
    public ResponseEntity<Void> reject(
            @Parameter(description = "신청 ID", required = true)
                    @PathVariable(SellerApplicationAdminEndpoints.PATH_APPLICATION_ID)
                    Long applicationId,
            @Valid @RequestBody RejectSellerApplicationApiRequest request) {

        RejectSellerApplicationCommand command = mapper.toRejectCommand(applicationId, request);
        rejectUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
