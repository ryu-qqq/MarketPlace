package com.ryuqq.marketplace.adapter.in.rest.auth.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import com.ryuqq.marketplace.adapter.in.rest.auth.AuthAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.MyInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.auth.mapper.AuthQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.port.in.GetMyInfoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthQueryController - 인증 정보 조회 API.
 *
 * <p>현재 로그인한 관리자 정보 조회 엔드포인트를 제공합니다.
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
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "인증", description = "관리자 인증 정보 조회 API")
@RestController
@RequestMapping(AuthAdminEndpoints.BASE)
public class AuthQueryController {

    private final GetMyInfoUseCase getMyInfoUseCase;
    private final AuthQueryApiMapper queryMapper;

    public AuthQueryController(GetMyInfoUseCase getMyInfoUseCase, AuthQueryApiMapper queryMapper) {
        this.getMyInfoUseCase = getMyInfoUseCase;
        this.queryMapper = queryMapper;
    }

    /**
     * 내 정보 조회 API.
     *
     * <p>현재 로그인한 관리자의 정보를 조회합니다.
     * Gateway가 전달한 X-User-Id 헤더에서 userId를 추출하여 Internal API로 조회합니다.
     *
     * @return 관리자 정보
     */
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 관리자의 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 요청")
    })
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "auth:read", description = "내 정보 조회")
    @GetMapping(AuthAdminEndpoints.ME)
    public ResponseEntity<ApiResponse<MyInfoApiResponse>> me() {

        String userId = UserContextHolder.getCurrentUserId();
        MyInfoResult result = getMyInfoUseCase.execute(userId);
        MyInfoApiResponse response = queryMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
