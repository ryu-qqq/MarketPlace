package com.ryuqq.marketplace.adapter.out.client.authhub.mapper;

import com.ryuqq.authhub.sdk.model.auth.LoginRequest;
import com.ryuqq.authhub.sdk.model.auth.LoginResponse;
import com.ryuqq.authhub.sdk.model.auth.LogoutRequest;
import com.ryuqq.authhub.sdk.model.auth.MyContextResponse;
import com.ryuqq.authhub.sdk.model.auth.RefreshTokenRequest;
import com.ryuqq.authhub.sdk.model.auth.TokenResponse;
import com.ryuqq.authhub.sdk.model.internal.UserContext;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubLoginResult;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubRefreshResult;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubUserContext;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * AuthHub 인증 Mapper.
 *
 * <p>Application DTO와 AuthHub SDK 객체 간의 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class AuthHubAuthMapper {

    // ========== Request 변환 ==========

    /**
     * 로그인 요청을 SDK 요청으로 변환합니다.
     *
     * @param identifier 사용자 식별자
     * @param password 비밀번호
     * @return SDK 로그인 요청
     */
    public LoginRequest toLoginRequest(String identifier, String password) {
        return new LoginRequest(identifier, password);
    }

    /**
     * 로그아웃 요청을 SDK 요청으로 변환합니다.
     *
     * @param userId 사용자 ID
     * @return SDK 로그아웃 요청
     */
    public LogoutRequest toLogoutRequest(String userId) {
        return new LogoutRequest(userId);
    }

    /**
     * 토큰 갱신 요청을 SDK 요청으로 변환합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return SDK 토큰 갱신 요청
     */
    public RefreshTokenRequest toRefreshTokenRequest(String refreshToken) {
        return new RefreshTokenRequest(refreshToken);
    }

    // ========== Response 변환 (Application DTO) ==========

    /**
     * SDK 로그인 응답을 Application DTO로 변환합니다.
     *
     * @param response SDK 로그인 응답
     * @return 로그인 결과
     */
    public LoginResult toLoginResult(LoginResponse response) {
        return LoginResult.success(
                response.userId(),
                response.accessToken(),
                response.refreshToken(),
                response.expiresIn(),
                response.tokenType());
    }

    /**
     * 로그인 실패 결과를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return 로그인 실패 결과
     */
    public LoginResult toLoginFailure(String errorCode, String errorMessage) {
        return LoginResult.failure(errorCode, errorMessage);
    }

    /**
     * SDK 사용자 정보 응답을 Application DTO로 변환합니다.
     *
     * @param response SDK 사용자 정보 응답
     * @return 사용자 정보 결과
     */
    public MyInfoResult toMyInfoResult(MyContextResponse response) {
        List<MyInfoResult.RoleInfo> roles =
                response.roles() != null
                        ? response.roles().stream()
                                .map(r -> new MyInfoResult.RoleInfo(r.id(), r.name()))
                                .toList()
                        : List.of();

        return new MyInfoResult(
                response.userId(),
                response.email(),
                response.name(),
                response.tenant() != null ? response.tenant().id() : null,
                response.tenant() != null ? response.tenant().name() : null,
                response.organization() != null ? response.organization().id() : null,
                response.organization() != null ? response.organization().name() : null,
                roles,
                response.permissions() != null ? response.permissions() : List.of(),
                null,
                null,
                response.phoneNumber());
    }

    /**
     * SDK 토큰 갱신 응답을 Application DTO로 변환합니다.
     *
     * @param response SDK 토큰 갱신 응답
     * @return 토큰 갱신 결과
     */
    public RefreshResult toRefreshResult(TokenResponse response) {
        return RefreshResult.success(
                response.accessToken(),
                response.refreshToken(),
                response.accessTokenExpiresIn(),
                "Bearer");
    }

    // ========== Response 변환 (Internal DTO) ==========

    /**
     * SDK 로그인 응답을 내부 DTO로 변환합니다.
     *
     * @param response SDK 로그인 응답
     * @return 내부 로그인 결과
     */
    public AuthHubLoginResult toAuthHubLoginResult(LoginResponse response) {
        return AuthHubLoginResult.success(
                response.userId(),
                response.accessToken(),
                response.refreshToken(),
                response.expiresIn(),
                response.tokenType());
    }

    /**
     * 내부 로그인 실패 결과를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return 내부 로그인 실패 결과
     */
    public AuthHubLoginResult toAuthHubLoginFailure(String errorCode, String errorMessage) {
        return AuthHubLoginResult.failure(errorCode, errorMessage);
    }

    /**
     * SDK 토큰 갱신 응답을 내부 DTO로 변환합니다.
     *
     * @param response SDK 토큰 갱신 응답
     * @return 내부 토큰 갱신 결과
     */
    public AuthHubRefreshResult toAuthHubRefreshResult(TokenResponse response) {
        return AuthHubRefreshResult.success(
                response.accessToken(),
                response.refreshToken(),
                response.accessTokenExpiresIn(),
                response.refreshTokenExpiresIn());
    }

    /**
     * 내부 토큰 갱신 실패 결과를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return 내부 토큰 갱신 실패 결과
     */
    public AuthHubRefreshResult toAuthHubRefreshFailure(String errorCode, String errorMessage) {
        return AuthHubRefreshResult.failure(errorCode, errorMessage);
    }

    /**
     * SDK 사용자 정보 응답을 내부 DTO로 변환합니다.
     *
     * @param response SDK 사용자 정보 응답
     * @return 내부 사용자 컨텍스트
     */
    public AuthHubUserContext toAuthHubUserContext(MyContextResponse response) {
        return AuthHubUserContext.from(response);
    }

    /**
     * Internal API UserContext 응답을 Application DTO로 변환합니다.
     *
     * @param context Internal API 사용자 컨텍스트 응답
     * @return 사용자 정보 결과
     */
    public MyInfoResult toMyInfoResultFromInternal(UserContext context) {
        List<MyInfoResult.RoleInfo> roles =
                context.roles() != null
                        ? context.roles().stream()
                                .map(r -> new MyInfoResult.RoleInfo(r.id(), r.name()))
                                .toList()
                        : List.of();

        return new MyInfoResult(
                context.userId(),
                context.email(),
                context.name(),
                context.tenant() != null ? context.tenant().id() : null,
                context.tenant() != null ? context.tenant().name() : null,
                context.organization() != null ? context.organization().id() : null,
                context.organization() != null ? context.organization().name() : null,
                roles,
                context.permissions() != null ? context.permissions() : List.of(),
                null,
                null,
                context.phoneNumber());
    }
}
