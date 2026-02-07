package com.ryuqq.marketplace.adapter.out.client.authhub.adapter;

import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.authhub.sdk.exception.AuthHubUnauthorizedException;
import com.ryuqq.authhub.sdk.model.auth.LoginResponse;
import com.ryuqq.authhub.sdk.model.auth.MyContextResponse;
import com.ryuqq.authhub.sdk.model.auth.TokenResponse;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubLoginResult;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubRefreshResult;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubUserContext;
import com.ryuqq.marketplace.adapter.out.client.authhub.mapper.AuthHubAuthMapper;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;
import com.ryuqq.marketplace.application.auth.port.out.client.AuthClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * AuthHub 인증 클라이언트 어댑터.
 *
 * <p>AuthHub SDK를 사용하여 로그인, 토큰 갱신, 사용자 정보 조회를 수행합니다.
 *
 * <p>AuthClient를 구현하여 Application Layer에서 추상화된 인터페이스로 접근할 수 있습니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnBean(AuthApi.class)
public class AuthHubAuthClientAdapter implements AuthClient {

    private final AuthApi authApi;
    private final AuthHubAuthMapper mapper;

    public AuthHubAuthClientAdapter(AuthApi authApi, AuthHubAuthMapper mapper) {
        this.authApi = authApi;
        this.mapper = mapper;
    }

    @Override
    public LoginResult login(String identifier, String password) {
        try {
            ApiResponse<LoginResponse> response =
                    authApi.login(mapper.toLoginRequest(identifier, password));

            return mapper.toLoginResult(response.data());

        } catch (AuthHubUnauthorizedException e) {
            return mapper.toLoginFailure("UNAUTHORIZED", e.getMessage());

        } catch (AuthHubException e) {
            return mapper.toLoginFailure("AUTH_ERROR", e.getMessage());
        }
    }

    @Override
    public void logout(String userId) {
        try {
            authApi.logout(mapper.toLogoutRequest(userId));

        } catch (AuthHubException e) {
            throw new IllegalStateException("Failed to logout: " + e.getMessage(), e);
        }
    }

    @Override
    public RefreshResult refresh(String refreshToken) {
        try {
            ApiResponse<TokenResponse> response =
                    authApi.refresh(mapper.toRefreshTokenRequest(refreshToken));

            return mapper.toRefreshResult(response.data());

        } catch (AuthHubUnauthorizedException e) {
            return RefreshResult.failure("UNAUTHORIZED", e.getMessage());

        } catch (AuthHubException e) {
            return RefreshResult.failure("AUTH_ERROR", e.getMessage());
        }
    }

    @Override
    public MyInfoResult getMyInfo(String accessToken) {
        try {
            // TODO: SDK에서 accessToken을 어떻게 처리하는지 확인 필요
            // 현재 SDK의 getMe()는 파라미터 없이 호출됨 (내부적으로 토큰 관리)
            ApiResponse<MyContextResponse> response = authApi.getMe();

            return mapper.toMyInfoResult(response.data());

        } catch (AuthHubUnauthorizedException e) {
            throw new IllegalStateException("Unauthorized: " + e.getMessage(), e);

        } catch (AuthHubException e) {
            throw new IllegalStateException("Failed to get user context: " + e.getMessage(), e);
        }
    }

    /**
     * 로그인을 수행합니다 (내부 DTO 반환).
     *
     * @param identifier 사용자 식별자 (이메일 또는 사용자명)
     * @param password 비밀번호
     * @return 로그인 결과 (AuthHub 내부 DTO)
     */
    public AuthHubLoginResult loginInternal(String identifier, String password) {
        try {
            ApiResponse<LoginResponse> response =
                    authApi.login(mapper.toLoginRequest(identifier, password));

            return mapper.toAuthHubLoginResult(response.data());

        } catch (AuthHubUnauthorizedException e) {
            return mapper.toAuthHubLoginFailure("UNAUTHORIZED", e.getMessage());

        } catch (AuthHubException e) {
            return mapper.toAuthHubLoginFailure("AUTH_ERROR", e.getMessage());
        }
    }

    /**
     * 토큰을 갱신합니다 (내부 DTO 반환).
     *
     * @param refreshToken 리프레시 토큰
     * @return 토큰 갱신 결과 (AuthHub 내부 DTO)
     */
    public AuthHubRefreshResult refreshInternal(String refreshToken) {
        try {
            ApiResponse<TokenResponse> response =
                    authApi.refresh(mapper.toRefreshTokenRequest(refreshToken));

            return mapper.toAuthHubRefreshResult(response.data());

        } catch (AuthHubUnauthorizedException e) {
            return mapper.toAuthHubRefreshFailure("UNAUTHORIZED", e.getMessage());

        } catch (AuthHubException e) {
            return mapper.toAuthHubRefreshFailure("AUTH_ERROR", e.getMessage());
        }
    }

    /**
     * 현재 사용자 정보를 조회합니다 (내부 DTO 반환).
     *
     * @return 사용자 컨텍스트 (AuthHub 내부 DTO)
     */
    public AuthHubUserContext getMe() {
        try {
            ApiResponse<MyContextResponse> response = authApi.getMe();

            return mapper.toAuthHubUserContext(response.data());

        } catch (AuthHubUnauthorizedException e) {
            throw new IllegalStateException("Unauthorized: " + e.getMessage(), e);

        } catch (AuthHubException e) {
            throw new IllegalStateException("Failed to get user context: " + e.getMessage(), e);
        }
    }
}
