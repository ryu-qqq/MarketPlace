package com.ryuqq.marketplace.adapter.in.rest.seller.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.SellerAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.RegisterSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerFullApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.mapper.SellerCommandApiMapper;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerFullCommand;
import com.ryuqq.marketplace.application.seller.port.in.command.RegisterSellerUseCase;
import com.ryuqq.marketplace.application.seller.port.in.command.UpdateSellerFullUseCase;
import com.ryuqq.marketplace.application.seller.port.in.command.UpdateSellerUseCase;
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
 * SellerCommandController - 셀러 커맨드 API.
 *
 * <p>셀러 생성, 수정 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-002: DELETE 메서드 금지 (소프트 삭제는 PATCH).
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-004: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>API-CTR-005: Controller @Transactional 금지.
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
@Tag(name = "셀러 커맨드", description = "셀러 커맨드 API (생성/수정)")
@RestController
@RequestMapping(SellerAdminEndpoints.SELLERS)
public class SellerCommandController {

    private final RegisterSellerUseCase registerSellerUseCase;
    private final UpdateSellerUseCase updateSellerUseCase;
    private final UpdateSellerFullUseCase updateSellerFullUseCase;
    private final SellerCommandApiMapper mapper;

    /**
     * SellerCommandController 생성자.
     *
     * @param registerSellerUseCase 셀러 등록 UseCase
     * @param updateSellerUseCase 셀러 기본정보 수정 UseCase
     * @param updateSellerFullUseCase 셀러 전체정보 수정 UseCase (CS 포함)
     * @param mapper Command API 매퍼
     */
    public SellerCommandController(
            RegisterSellerUseCase registerSellerUseCase,
            UpdateSellerUseCase updateSellerUseCase,
            UpdateSellerFullUseCase updateSellerFullUseCase,
            SellerCommandApiMapper mapper) {
        this.registerSellerUseCase = registerSellerUseCase;
        this.updateSellerUseCase = updateSellerUseCase;
        this.updateSellerFullUseCase = updateSellerFullUseCase;
        this.mapper = mapper;
    }

    /**
     * 셀러 등록 API.
     *
     * <p>새로운 셀러를 등록합니다.
     *
     * <p><b>권한 요구사항:</b> 최고 마스터 권한 필요 (SUPER_ADMIN)
     *
     * <p>TODO: 권한 체크 구현 필요 - Gateway에서 처리 또는 @PreAuthorize 적용
     *
     * @param request 등록 요청 DTO
     * @return 생성된 셀러 ID
     */
    @Operation(summary = "셀러 등록", description = "새로운 셀러를 등록합니다. (최고 마스터 권한 필요)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.hasPermission('seller:write')")
    @RequirePermission(value = "seller:write", description = "셀러 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<SellerIdApiResponse>> registerSeller(
            @Valid @RequestBody RegisterSellerApiRequest request) {

        RegisterSellerCommand command = mapper.toCommand(request);
        Long sellerId = registerSellerUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(SellerIdApiResponse.of(sellerId)));
    }

    /**
     * 셀러 전체정보 수정 API.
     *
     * <p>셀러의 기본정보, 사업자정보, CS정보, 주소를 한번에 수정합니다.
     *
     * <p><b>권한 요구사항:</b> 최고 마스터 권한 필요 (SUPER_ADMIN)
     *
     * <p>TODO: 권한 체크 구현 필요 - Gateway에서 처리 또는 @PreAuthorize 적용
     *
     * <p>TODO: 계약정보(ContractInfo), 정산정보(SettlementInfo) 추가 예정 - UseCase 구현 후
     *
     * @param sellerId 셀러 ID
     * @param request 전체 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(
            summary = "셀러 전체정보 수정",
            description = "셀러의 기본정보, 사업자정보, CS정보, 주소를 한번에 수정합니다. (최고 마스터 권한 필요)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "셀러를 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('seller:write')")
    @RequirePermission(value = "seller:write", description = "셀러 전체정보 수정")
    @PutMapping(SellerAdminEndpoints.SELLER_ID)
    public ResponseEntity<Void> updateSellerFull(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(SellerAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Valid @RequestBody UpdateSellerFullApiRequest request) {

        UpdateSellerFullCommand command = mapper.toCommand(sellerId, request);
        updateSellerFullUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 셀러 기본정보 수정 API.
     *
     * <p>셀러의 기본정보(셀러명, 표시명, 로고, 설명)만 수정합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 기본정보 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "셀러 기본정보 수정", description = "셀러의 기본정보만 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "셀러를 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'seller:write')")
    @RequirePermission(value = "seller:write", description = "셀러 기본정보 수정")
    @PatchMapping(SellerAdminEndpoints.SELLER_ID)
    public ResponseEntity<Void> updateSeller(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(SellerAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Valid @RequestBody UpdateSellerApiRequest request) {

        UpdateSellerCommand command = mapper.toCommand(sellerId, request);
        updateSellerUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
