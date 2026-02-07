package com.ryuqq.marketplace.adapter.out.client.authhub.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.authhub.sdk.exception.AuthHubUnauthorizedException;
import com.ryuqq.authhub.sdk.model.auth.LoginRequest;
import com.ryuqq.authhub.sdk.model.auth.LoginResponse;
import com.ryuqq.authhub.sdk.model.auth.LogoutRequest;
import com.ryuqq.authhub.sdk.model.auth.MyContextResponse;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.marketplace.adapter.out.client.authhub.mapper.AuthHubAuthMapper;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * AuthHubAuthClientAdapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthHubAuthClientAdapter 단위 테스트")
class AuthHubAuthClientAdapterTest {

    @InjectMocks private AuthHubAuthClientAdapter sut;

    @Mock private AuthApi authApi;
    @Mock private AuthHubAuthMapper mapper;

    private static final String DEFAULT_IDENTIFIER = "admin@example.com";
    private static final String DEFAULT_PASSWORD = "password123!";
    private static final String DEFAULT_USER_ID = "user-123";
    private static final String DEFAULT_ACCESS_TOKEN = "access-token";

    @Nested
    @DisplayName("login() - 로그인")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공 시 결과를 반환한다")
        @SuppressWarnings("unchecked")
        void login_Success() {
            // given
            LoginRequest loginRequest = new LoginRequest(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD);
            LoginResponse loginResponse =
                    new LoginResponse(
                            DEFAULT_USER_ID,
                            DEFAULT_ACCESS_TOKEN,
                            "refresh-token",
                            3600L,
                            "Bearer");
            ApiResponse<LoginResponse> apiResponse = mock(ApiResponse.class);
            given(apiResponse.data()).willReturn(loginResponse);

            LoginResult expectedResult =
                    LoginResult.success(
                            DEFAULT_USER_ID,
                            DEFAULT_ACCESS_TOKEN,
                            "refresh-token",
                            3600L,
                            "Bearer");

            given(mapper.toLoginRequest(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD))
                    .willReturn(loginRequest);
            given(authApi.login(loginRequest)).willReturn(apiResponse);
            given(mapper.toLoginResult(loginResponse)).willReturn(expectedResult);

            // when
            LoginResult result = sut.login(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.accessToken()).isEqualTo(DEFAULT_ACCESS_TOKEN);
            then(authApi).should().login(loginRequest);
        }

        @Test
        @DisplayName("인증 실패 시 실패 결과를 반환한다")
        void login_Unauthorized_ReturnsFailure() {
            // given
            LoginRequest loginRequest = new LoginRequest(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD);
            LoginResult failureResult = LoginResult.failure("UNAUTHORIZED", "Invalid credentials");

            given(mapper.toLoginRequest(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD))
                    .willReturn(loginRequest);
            given(authApi.login(loginRequest))
                    .willThrow(
                            new AuthHubUnauthorizedException(
                                    "UNAUTHORIZED", "Invalid credentials"));
            given(mapper.toLoginFailure(eq("UNAUTHORIZED"), anyString())).willReturn(failureResult);

            // when
            LoginResult result = sut.login(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD);

            // then
            assertThat(result.isFailure()).isTrue();
            assertThat(result.errorCode()).isEqualTo("UNAUTHORIZED");
        }

        @Test
        @DisplayName("AuthHub 에러 시 실패 결과를 반환한다")
        void login_AuthHubError_ReturnsFailure() {
            // given
            LoginRequest loginRequest = new LoginRequest(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD);
            LoginResult failureResult = LoginResult.failure("AUTH_ERROR", "Auth error");

            given(mapper.toLoginRequest(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD))
                    .willReturn(loginRequest);
            given(authApi.login(loginRequest))
                    .willThrow(new AuthHubException(500, "AUTH_ERROR", "Auth error"));
            given(mapper.toLoginFailure(eq("AUTH_ERROR"), anyString())).willReturn(failureResult);

            // when
            LoginResult result = sut.login(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD);

            // then
            assertThat(result.isFailure()).isTrue();
            assertThat(result.errorCode()).isEqualTo("AUTH_ERROR");
        }
    }

    @Nested
    @DisplayName("logout() - 로그아웃")
    class LogoutTest {

        @Test
        @DisplayName("로그아웃을 수행한다")
        void logout_Success() {
            // given
            LogoutRequest logoutRequest = new LogoutRequest(DEFAULT_USER_ID);

            given(mapper.toLogoutRequest(DEFAULT_USER_ID)).willReturn(logoutRequest);
            willDoNothing().given(authApi).logout(logoutRequest);

            // when
            sut.logout(DEFAULT_USER_ID);

            // then
            then(authApi).should().logout(logoutRequest);
        }

        @Test
        @DisplayName("로그아웃 실패 시 예외를 발생시킨다")
        void logout_Failed_ThrowsException() {
            // given
            LogoutRequest logoutRequest = new LogoutRequest(DEFAULT_USER_ID);

            given(mapper.toLogoutRequest(DEFAULT_USER_ID)).willReturn(logoutRequest);
            willThrow(new AuthHubException(500, "LOGOUT_ERROR", "Logout failed"))
                    .given(authApi)
                    .logout(logoutRequest);

            // when & then
            assertThatThrownBy(() -> sut.logout(DEFAULT_USER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to logout");
        }
    }

    @Nested
    @DisplayName("getMyInfo() - 내 정보 조회")
    class GetMyInfoTest {

        @Test
        @DisplayName("내 정보를 조회한다")
        @SuppressWarnings("unchecked")
        void getMyInfo_Success() {
            // given
            MyContextResponse contextResponse = mock(MyContextResponse.class);
            ApiResponse<MyContextResponse> apiResponse = mock(ApiResponse.class);
            given(apiResponse.data()).willReturn(contextResponse);

            MyInfoResult expectedResult =
                    new MyInfoResult(
                            DEFAULT_USER_ID,
                            DEFAULT_IDENTIFIER,
                            "관리자",
                            "tenant-123",
                            "테넌트명",
                            "org-123",
                            "조직명",
                            List.of(new MyInfoResult.RoleInfo("role-1", "ADMIN")),
                            List.of("READ", "WRITE"));

            given(authApi.getMe()).willReturn(apiResponse);
            given(mapper.toMyInfoResult(contextResponse)).willReturn(expectedResult);

            // when
            MyInfoResult result = sut.getMyInfo(DEFAULT_ACCESS_TOKEN);

            // then
            assertThat(result.userId()).isEqualTo(DEFAULT_USER_ID);
            then(authApi).should().getMe();
        }

        @Test
        @DisplayName("인증 실패 시 예외를 발생시킨다")
        void getMyInfo_Unauthorized_ThrowsException() {
            // given
            given(authApi.getMe())
                    .willThrow(new AuthHubUnauthorizedException("UNAUTHORIZED", "Unauthorized"));

            // when & then
            assertThatThrownBy(() -> sut.getMyInfo(DEFAULT_ACCESS_TOKEN))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Unauthorized");
        }
    }
}
