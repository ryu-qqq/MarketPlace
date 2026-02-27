package com.ryuqq.marketplace.adapter.in.rest.commoncodetype.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.CommonCodeTypeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.ChangeActiveStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.RegisterCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.UpdateCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.mapper.CommonCodeTypeCommandApiMapper;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.ChangeActiveStatusCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.RegisterCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.UpdateCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.port.in.command.ChangeCommonCodeTypeStatusUseCase;
import com.ryuqq.marketplace.application.commoncodetype.port.in.command.RegisterCommonCodeTypeUseCase;
import com.ryuqq.marketplace.application.commoncodetype.port.in.command.UpdateCommonCodeTypeUseCase;
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
 * CommonCodeTypeCommandController - 공통 코드 타입 생성/수정 API.
 *
 * <p>공통 코드 타입 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "공통 코드 타입 관리", description = "공통 코드 타입 생성/수정 API")
@RestController
@RequestMapping(CommonCodeTypeAdminEndpoints.COMMON_CODE_TYPES)
public class CommonCodeTypeCommandController {

    private final RegisterCommonCodeTypeUseCase registerUseCase;
    private final UpdateCommonCodeTypeUseCase updateUseCase;
    private final ChangeCommonCodeTypeStatusUseCase changeStatusUseCase;
    private final CommonCodeTypeCommandApiMapper mapper;

    /**
     * CommonCodeTypeCommandController 생성자.
     *
     * @param registerUseCase 공통 코드 타입 등록 UseCase
     * @param updateUseCase 공통 코드 타입 수정 UseCase
     * @param changeStatusUseCase 공통 코드 타입 상태 변경 UseCase
     * @param mapper Command API 매퍼
     */
    public CommonCodeTypeCommandController(
            RegisterCommonCodeTypeUseCase registerUseCase,
            UpdateCommonCodeTypeUseCase updateUseCase,
            ChangeCommonCodeTypeStatusUseCase changeStatusUseCase,
            CommonCodeTypeCommandApiMapper mapper) {
        this.registerUseCase = registerUseCase;
        this.updateUseCase = updateUseCase;
        this.changeStatusUseCase = changeStatusUseCase;
        this.mapper = mapper;
    }

    /**
     * 공통 코드 타입 등록 API.
     *
     * <p>새로운 공통 코드 타입을 등록합니다.
     *
     * @param request 등록 요청 DTO
     * @return 생성된 공통 코드 타입 ID
     */
    @Operation(summary = "공통 코드 타입 등록", description = "새로운 공통 코드 타입을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 코드")
    })
    @PreAuthorize("@access.hasPermission('common-code-type:write')")
    @RequirePermission(value = "common-code-type:write", description = "공통 코드 타입 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> register(
            @Valid @RequestBody RegisterCommonCodeTypeApiRequest request) {

        RegisterCommonCodeTypeCommand command = mapper.toCommand(request);
        Long createdId = registerUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(createdId));
    }

    /**
     * 공통 코드 타입 수정 API.
     *
     * <p>기존 공통 코드 타입의 정보를 수정합니다.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "공통 코드 타입 수정", description = "공통 코드 타입의 이름, 설명, 순서를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "공통 코드 타입을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('common-code-type:write')")
    @RequirePermission(value = "common-code-type:write", description = "공통 코드 타입 수정")
    @PutMapping(CommonCodeTypeAdminEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "공통 코드 타입 ID", required = true)
                    @PathVariable(CommonCodeTypeAdminEndpoints.PATH_COMMON_CODE_TYPE_ID)
                    Long commonCodeTypeId,
            @Valid @RequestBody UpdateCommonCodeTypeApiRequest request) {

        UpdateCommonCodeTypeCommand command = mapper.toCommand(commonCodeTypeId, request);
        updateUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }

    /**
     * 공통 코드 타입 활성화/비활성화 API.
     *
     * <p>선택한 공통 코드 타입들의 활성화 상태를 변경합니다.
     *
     * @param request 상태 변경 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "공통 코드 타입 활성화/비활성화", description = "선택한 공통 코드 타입들의 활성화 상태를 변경합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.hasPermission('common-code-type:write')")
    @RequirePermission(value = "common-code-type:write", description = "공통 코드 타입 활성화 상태 변경")
    @PatchMapping(CommonCodeTypeAdminEndpoints.ACTIVE_STATUS)
    public ResponseEntity<ApiResponse<Void>> changeActiveStatus(
            @Valid @RequestBody ChangeActiveStatusApiRequest request) {

        ChangeActiveStatusCommand command = mapper.toCommand(request);
        changeStatusUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
