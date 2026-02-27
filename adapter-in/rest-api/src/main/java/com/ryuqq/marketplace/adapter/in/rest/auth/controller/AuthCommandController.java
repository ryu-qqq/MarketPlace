package com.ryuqq.marketplace.adapter.in.rest.auth.controller;

import com.ryuqq.authhub.sdk.context.UserContextHolder;
import com.ryuqq.marketplace.adapter.in.rest.auth.AuthPublicEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.auth.config.AuthCookieProperties;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.command.RefreshApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.RefreshApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.auth.mapper.AuthCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.auth.dto.command.LoginCommand;
import com.ryuqq.marketplace.application.auth.dto.command.RefreshCommand;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;
import com.ryuqq.marketplace.application.auth.port.in.LoginUseCase;
import com.ryuqq.marketplace.application.auth.port.in.LogoutUseCase;
import com.ryuqq.marketplace.application.auth.port.in.RefreshTokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthCommandController - 인증 Command API.
 *
 * <p>로그인/로그아웃 엔드포인트를 제공합니다.
 *
 * <p>로그인/토큰갱신 시 Response Body와 Set-Cookie 헤더를 동시에 내려줍니다. 쿠키는 게이트웨이의 JwtAuthenticationFilter,
 * TokenRefreshFilter와 연동됩니다.
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
@Tag(name = "인증", description = "로그인/로그아웃 API")
@RestController
@RequestMapping(AuthPublicEndpoints.BASE)
public class AuthCommandController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final AuthCommandApiMapper commandMapper;
    private final AuthCookieProperties cookieProperties;

    public AuthCommandController(
            LoginUseCase loginUseCase,
            LogoutUseCase logoutUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            AuthCommandApiMapper commandMapper,
            AuthCookieProperties cookieProperties) {
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.commandMapper = commandMapper;
        this.cookieProperties = cookieProperties;
    }

    /**
     * 로그인 API.
     *
     * <p>사용자 인증 후 액세스 토큰을 발급합니다. Response Body와 Set-Cookie 헤더를 동시에 내려줍니다.
     *
     * @param request 로그인 요청 DTO
     * @return 액세스 토큰
     */
    @Operation(summary = "로그인", description = "사용자 인증 후 액세스 토큰을 발급합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "로그인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증 실패")
    })
    @PostMapping(AuthPublicEndpoints.LOGIN)
    public ResponseEntity<ApiResponse<LoginApiResponse>> login(
            @Valid @RequestBody LoginApiRequest request) {

        LoginCommand command = commandMapper.toCommand(request);
        LoginResult result = loginUseCase.execute(command);
        LoginApiResponse response = commandMapper.toResponse(result);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        buildAccessCookie(response.accessToken(), cookieProperties.accessMaxAge())
                                .toString())
                .header(
                        HttpHeaders.SET_COOKIE,
                        buildRefreshCookie(
                                        response.refreshToken(), cookieProperties.refreshMaxAge())
                                .toString())
                .body(ApiResponse.of(response));
    }

    /**
     * 로그아웃 API.
     *
     * <p>현재 세션의 토큰을 무효화하고 인증 쿠키를 삭제합니다.
     *
     * @return 빈 응답
     */
    @Operation(summary = "로그아웃", description = "현재 세션의 토큰을 무효화합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "로그아웃 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 요청")
    })
    @PostMapping(AuthPublicEndpoints.LOGOUT)
    public ResponseEntity<ApiResponse<Void>> logout() {

        String userId = UserContextHolder.getCurrentUserId();
        logoutUseCase.execute(commandMapper.toCommand(userId));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildAccessCookie("", 0).toString())
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie("", 0).toString())
                .body(ApiResponse.of());
    }

    /**
     * 토큰 갱신 API.
     *
     * <p>리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다. Response Body와 Set-Cookie 헤더를 동시에 내려줍니다.
     *
     * @param request 토큰 갱신 요청 DTO
     * @return 갱신된 토큰 정보
     */
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "토큰 갱신 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "유효하지 않은 리프레시 토큰")
    })
    @PostMapping(AuthPublicEndpoints.REFRESH)
    public ResponseEntity<ApiResponse<RefreshApiResponse>> refresh(
            @Valid @RequestBody RefreshApiRequest request) {

        RefreshCommand command = commandMapper.toRefreshCommand(request);
        RefreshResult result = refreshTokenUseCase.execute(command);
        RefreshApiResponse response = commandMapper.toRefreshResponse(result);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        buildAccessCookie(response.accessToken(), cookieProperties.accessMaxAge())
                                .toString())
                .header(
                        HttpHeaders.SET_COOKIE,
                        buildRefreshCookie(
                                        response.refreshToken(), cookieProperties.refreshMaxAge())
                                .toString())
                .body(ApiResponse.of(response));
    }

    private ResponseCookie buildAccessCookie(String value, long maxAge) {
        return buildCookie(AuthCookieProperties.ACCESS_TOKEN_COOKIE, value, maxAge);
    }

    private ResponseCookie buildRefreshCookie(String value, long maxAge) {
        return buildCookie(AuthCookieProperties.REFRESH_TOKEN_COOKIE, value, maxAge);
    }

    private ResponseCookie buildCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(cookieProperties.httpOnly())
                .secure(cookieProperties.secure())
                .path(cookieProperties.path())
                .sameSite(cookieProperties.sameSite())
                .maxAge(Duration.ofSeconds(maxAge))
                .build();
    }
}
