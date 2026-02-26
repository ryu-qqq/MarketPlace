package com.ryuqq.marketplace.adapter.in.rest.selleradmin.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminApplicationEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ApplySellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkApproveSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkRejectSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ChangeSellerAdminPasswordApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.ApplySellerAdminApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.ApproveSellerAdminApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.BulkApproveSellerAdminApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.mapper.SellerAdminApplicationCommandApiMapper;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApplySellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkRejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ChangeSellerAdminPasswordCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ResetSellerAdminPasswordCommand;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ApplySellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ApproveSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.BulkApproveSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.BulkRejectSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ChangeSellerAdminPasswordUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.RejectSellerAdminUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ResetSellerAdminPasswordUseCase;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SellerAdminApplicationCommandController - 셀러 관리자 가입 신청 Command API.
 *
 * <p>셀러 관리자 가입 신청, 승인, 거절 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: Controller는 @RestController로 정의.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-004: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
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
 * @since 1.1.0
 */
@Tag(name = "셀러 관리자 가입 신청", description = "셀러 관리자 가입 신청/승인/거절 API")
@RestController
@RequestMapping(SellerAdminApplicationEndpoints.BASE)
public class SellerAdminApplicationCommandController {

    private final ApplySellerAdminUseCase applyUseCase;
    private final ApproveSellerAdminUseCase approveUseCase;
    private final RejectSellerAdminUseCase rejectUseCase;
    private final BulkApproveSellerAdminUseCase bulkApproveUseCase;
    private final BulkRejectSellerAdminUseCase bulkRejectUseCase;
    private final ResetSellerAdminPasswordUseCase resetPasswordUseCase;
    private final ChangeSellerAdminPasswordUseCase changePasswordUseCase;
    private final SellerAdminApplicationCommandApiMapper mapper;

    /**
     * SellerAdminApplicationCommandController 생성자.
     *
     * @param applyUseCase 가입 신청 UseCase
     * @param approveUseCase 승인 UseCase
     * @param rejectUseCase 거절 UseCase
     * @param bulkApproveUseCase 일괄 승인 UseCase
     * @param bulkRejectUseCase 일괄 거절 UseCase
     * @param resetPasswordUseCase 비밀번호 초기화 UseCase
     * @param changePasswordUseCase 비밀번호 변경 UseCase
     * @param mapper Command API 매퍼
     */
    public SellerAdminApplicationCommandController(
            ApplySellerAdminUseCase applyUseCase,
            ApproveSellerAdminUseCase approveUseCase,
            RejectSellerAdminUseCase rejectUseCase,
            BulkApproveSellerAdminUseCase bulkApproveUseCase,
            BulkRejectSellerAdminUseCase bulkRejectUseCase,
            ResetSellerAdminPasswordUseCase resetPasswordUseCase,
            ChangeSellerAdminPasswordUseCase changePasswordUseCase,
            SellerAdminApplicationCommandApiMapper mapper) {
        this.applyUseCase = applyUseCase;
        this.approveUseCase = approveUseCase;
        this.rejectUseCase = rejectUseCase;
        this.bulkApproveUseCase = bulkApproveUseCase;
        this.bulkRejectUseCase = bulkRejectUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.mapper = mapper;
    }

    /**
     * 셀러 관리자 가입 신청 API.
     *
     * <p>셀러 하위에 새로운 관리자 가입을 신청합니다.
     *
     * @param request 가입 신청 요청 DTO (sellerId 포함)
     * @return 생성된 셀러 관리자 ID
     */
    @Operation(summary = "셀러 관리자 가입 신청", description = "셀러 하위에 새로운 관리자 가입을 신청합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "신청 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "셀러를 찾을 수 없음")
    })
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "seller-admin:write", description = "셀러 관리자 가입 신청")
    @PostMapping
    public ResponseEntity<ApiResponse<ApplySellerAdminApiResponse>> apply(
            @Valid @RequestBody ApplySellerAdminApiRequest request) {

        ApplySellerAdminCommand command = mapper.toCommand(request);
        String sellerAdminId = applyUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(new ApplySellerAdminApiResponse(sellerAdminId)));
    }

    /**
     * 셀러 관리자 가입 신청 승인 API.
     *
     * <p>대기 중인 가입 신청을 승인하고 인증 서버에 사용자를 생성합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return 승인 결과 (authUserId 포함)
     */
    @Operation(summary = "셀러 관리자 가입 신청 승인", description = "대기 중인 가입 신청을 승인하고 인증 서버에 사용자를 생성합니다.")
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
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "seller-admin:approve", description = "셀러 관리자 가입 승인")
    @PostMapping(SellerAdminApplicationEndpoints.APPROVE)
    public ResponseEntity<ApiResponse<ApproveSellerAdminApiResponse>> approve(
            @Parameter(description = "셀러 관리자 ID", required = true)
                    @PathVariable(SellerAdminApplicationEndpoints.PATH_SELLER_ADMIN_ID)
                    String sellerAdminId) {

        ApproveSellerAdminCommand command = mapper.toApproveCommand(sellerAdminId);
        String savedSellerAdminId = approveUseCase.execute(command);

        return ResponseEntity.ok(
                ApiResponse.of(ApproveSellerAdminApiResponse.from(savedSellerAdminId)));
    }

    /**
     * 셀러 관리자 가입 신청 거절 API.
     *
     * <p>대기 중인 가입 신청을 거절합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "셀러 관리자 가입 신청 거절", description = "대기 중인 가입 신청을 거절합니다.")
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
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "seller-admin:reject", description = "셀러 관리자 가입 거절")
    @PostMapping(SellerAdminApplicationEndpoints.REJECT)
    public ResponseEntity<Void> reject(
            @Parameter(description = "셀러 관리자 ID", required = true)
                    @PathVariable(SellerAdminApplicationEndpoints.PATH_SELLER_ADMIN_ID)
                    String sellerAdminId) {

        RejectSellerAdminCommand command = mapper.toRejectCommand(sellerAdminId);
        rejectUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 셀러 관리자 가입 신청 일괄 승인 API.
     *
     * <p>여러 건의 가입 신청을 한 번에 승인합니다.
     *
     * @param request 일괄 승인 요청 DTO
     * @return 승인된 ID 목록
     */
    @Operation(summary = "셀러 관리자 가입 신청 일괄 승인", description = "여러 건의 가입 신청을 한 번에 승인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "일괄 승인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "seller-admin:approve", description = "셀러 관리자 일괄 승인")
    @PostMapping(SellerAdminApplicationEndpoints.BULK_APPROVE)
    public ResponseEntity<ApiResponse<BulkApproveSellerAdminApiResponse>> bulkApprove(
            @Valid @RequestBody BulkApproveSellerAdminApiRequest request) {

        BulkApproveSellerAdminCommand command = mapper.toBulkApproveCommand(request);
        BatchProcessingResult<String> result = bulkApproveUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }

    /**
     * 셀러 관리자 가입 신청 일괄 거절 API.
     *
     * <p>여러 건의 가입 신청을 한 번에 거절합니다.
     *
     * @param request 일괄 거절 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "셀러 관리자 가입 신청 일괄 거절", description = "여러 건의 가입 신청을 한 번에 거절합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "일괄 거절 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "seller-admin:reject", description = "셀러 관리자 일괄 거절")
    @PostMapping(SellerAdminApplicationEndpoints.BULK_REJECT)
    public ResponseEntity<Void> bulkReject(
            @Valid @RequestBody BulkRejectSellerAdminApiRequest request) {

        BulkRejectSellerAdminCommand command = mapper.toBulkRejectCommand(request);
        bulkRejectUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 셀러 관리자 비밀번호 초기화 API.
     *
     * <p>ACTIVE 상태의 셀러 관리자 비밀번호를 초기화합니다. 인증 서버에서 임시 비밀번호를 생성하고 사용자에게 전달합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "셀러 관리자 비밀번호 초기화", description = "ACTIVE 상태의 셀러 관리자 비밀번호를 초기화합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "비밀번호 초기화 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "비밀번호 초기화 불가 상태"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "셀러 관리자를 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "seller-admin:manage", description = "셀러 관리자 비밀번호 초기화")
    @PostMapping(SellerAdminApplicationEndpoints.RESET_PASSWORD)
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "셀러 관리자 ID", required = true)
                    @PathVariable(SellerAdminApplicationEndpoints.PATH_SELLER_ADMIN_ID)
                    String sellerAdminId) {

        ResetSellerAdminPasswordCommand command = mapper.toResetPasswordCommand(sellerAdminId);
        resetPasswordUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 셀러 관리자 비밀번호 변경 API.
     *
     * <p>외부 본인인증(이메일/문자 등) 완료 후 호출합니다. ACTIVE 상태의 셀러 관리자 비밀번호를 새 비밀번호로 변경합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @param request 비밀번호 변경 요청 (newPassword)
     * @return 빈 응답 (204 No Content)
     */
    @Operation(
            summary = "셀러 관리자 비밀번호 변경",
            description = "본인인증 완료 후 새 비밀번호로 변경합니다. 현재 비밀번호는 사용하지 않습니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "비밀번호 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 또는 비밀번호 변경 불가 상태"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "셀러 관리자를 찾을 수 없음")
    })
    @PreAuthorize("@access.myselfOr(#sellerAdminId, 'seller-admin:manage')")
    @RequirePermission(value = "seller-admin:manage", description = "셀러 관리자 비밀번호 변경")
    @PatchMapping(SellerAdminApplicationEndpoints.CHANGE_PASSWORD)
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "셀러 관리자 ID", required = true)
                    @PathVariable(SellerAdminApplicationEndpoints.PATH_SELLER_ADMIN_ID)
                    String sellerAdminId,
            @Valid @RequestBody ChangeSellerAdminPasswordApiRequest request) {

        ChangeSellerAdminPasswordCommand command =
                mapper.toChangePasswordCommand(sellerAdminId, request);
        changePasswordUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
