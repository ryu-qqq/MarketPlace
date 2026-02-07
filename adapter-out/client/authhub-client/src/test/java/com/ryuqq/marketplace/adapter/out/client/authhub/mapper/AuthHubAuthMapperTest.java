package com.ryuqq.marketplace.adapter.out.client.authhub.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.ryuqq.authhub.sdk.model.auth.LoginRequest;
import com.ryuqq.authhub.sdk.model.auth.LoginResponse;
import com.ryuqq.authhub.sdk.model.auth.LogoutRequest;
import com.ryuqq.authhub.sdk.model.auth.MyContextResponse;
import com.ryuqq.authhub.sdk.model.auth.RefreshTokenRequest;
import com.ryuqq.authhub.sdk.model.auth.TokenResponse;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubLoginResult;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubRefreshResult;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubUserContext;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * AuthHubAuthMapper 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("AuthHubAuthMapper 단위 테스트")
class AuthHubAuthMapperTest {

    private AuthHubAuthMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AuthHubAuthMapper();
    }

    @Nested
    @DisplayName("Request 변환")
    class RequestConversionTest {

        @Test
        @DisplayName("LoginRequest로 변환한다")
        void toLoginRequest_Success() {
            // given
            String identifier = "admin@example.com";
            String password = "password123";

            // when
            LoginRequest request = mapper.toLoginRequest(identifier, password);

            // then
            assertThat(request).isNotNull();
            assertThat(request.identifier()).isEqualTo(identifier);
            assertThat(request.password()).isEqualTo(password);
        }

        @Test
        @DisplayName("LogoutRequest로 변환한다")
        void toLogoutRequest_Success() {
            // given
            String userId = "user-123";

            // when
            LogoutRequest request = mapper.toLogoutRequest(userId);

            // then
            assertThat(request).isNotNull();
            assertThat(request.userId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("RefreshTokenRequest로 변환한다")
        void toRefreshTokenRequest_Success() {
            // given
            String refreshToken = "refresh-token";

            // when
            RefreshTokenRequest request = mapper.toRefreshTokenRequest(refreshToken);

            // then
            assertThat(request).isNotNull();
            assertThat(request.refreshToken()).isEqualTo(refreshToken);
        }
    }

    @Nested
    @DisplayName("Application DTO 변환")
    class ApplicationDtoConversionTest {

        @Test
        @DisplayName("SDK 로그인 응답을 LoginResult로 변환한다")
        void toLoginResult_Success() {
            // given
            LoginResponse response =
                    new LoginResponse("user-123", "access-token", "refresh-token", 3600L, "Bearer");

            // when
            LoginResult result = mapper.toLoginResult(response);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.userId()).isEqualTo("user-123");
            assertThat(result.accessToken()).isEqualTo("access-token");
            assertThat(result.refreshToken()).isEqualTo("refresh-token");
            assertThat(result.expiresIn()).isEqualTo(3600L);
            assertThat(result.tokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("로그인 실패 결과를 생성한다")
        void toLoginFailure_Success() {
            // given
            String errorCode = "UNAUTHORIZED";
            String errorMessage = "Invalid credentials";

            // when
            LoginResult result = mapper.toLoginFailure(errorCode, errorMessage);

            // then
            assertThat(result.isFailure()).isTrue();
            assertThat(result.errorCode()).isEqualTo(errorCode);
            assertThat(result.errorMessage()).isEqualTo(errorMessage);
        }

        @Test
        @DisplayName("SDK 사용자 정보 응답을 MyInfoResult로 변환한다")
        void toMyInfoResult_Success() {
            // given
            MyContextResponse.TenantInfo tenantInfo = mock(MyContextResponse.TenantInfo.class);
            given(tenantInfo.id()).willReturn("tenant-123");
            given(tenantInfo.name()).willReturn("테넌트명");

            MyContextResponse.OrganizationInfo orgInfo =
                    mock(MyContextResponse.OrganizationInfo.class);
            given(orgInfo.id()).willReturn("org-123");
            given(orgInfo.name()).willReturn("조직명");

            MyContextResponse.RoleInfo roleInfo = mock(MyContextResponse.RoleInfo.class);
            given(roleInfo.id()).willReturn("role-1");
            given(roleInfo.name()).willReturn("ADMIN");

            MyContextResponse response = mock(MyContextResponse.class);
            given(response.userId()).willReturn("user-123");
            given(response.email()).willReturn("admin@example.com");
            given(response.name()).willReturn("관리자");
            given(response.tenant()).willReturn(tenantInfo);
            given(response.organization()).willReturn(orgInfo);
            given(response.roles()).willReturn(List.of(roleInfo));
            given(response.permissions()).willReturn(List.of("READ", "WRITE"));

            // when
            MyInfoResult result = mapper.toMyInfoResult(response);

            // then
            assertThat(result.userId()).isEqualTo("user-123");
            assertThat(result.email()).isEqualTo("admin@example.com");
            assertThat(result.name()).isEqualTo("관리자");
            assertThat(result.tenantId()).isEqualTo("tenant-123");
            assertThat(result.tenantName()).isEqualTo("테넌트명");
            assertThat(result.organizationId()).isEqualTo("org-123");
            assertThat(result.organizationName()).isEqualTo("조직명");
            assertThat(result.roles()).hasSize(1);
            assertThat(result.permissions()).containsExactly("READ", "WRITE");
        }

        @Test
        @DisplayName("null인 tenant/organization도 처리한다")
        void toMyInfoResult_NullTenantAndOrg() {
            // given
            MyContextResponse response = mock(MyContextResponse.class);
            given(response.userId()).willReturn("user-123");
            given(response.email()).willReturn("admin@example.com");
            given(response.name()).willReturn("관리자");
            given(response.tenant()).willReturn(null);
            given(response.organization()).willReturn(null);
            given(response.roles()).willReturn(null);
            given(response.permissions()).willReturn(null);

            // when
            MyInfoResult result = mapper.toMyInfoResult(response);

            // then
            assertThat(result.tenantId()).isNull();
            assertThat(result.tenantName()).isNull();
            assertThat(result.organizationId()).isNull();
            assertThat(result.organizationName()).isNull();
            assertThat(result.roles()).isEmpty();
            assertThat(result.permissions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Internal DTO 변환")
    class InternalDtoConversionTest {

        @Test
        @DisplayName("SDK 로그인 응답을 AuthHubLoginResult로 변환한다")
        void toAuthHubLoginResult_Success() {
            // given
            LoginResponse response =
                    new LoginResponse("user-123", "access-token", "refresh-token", 3600L, "Bearer");

            // when
            AuthHubLoginResult result = mapper.toAuthHubLoginResult(response);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.userId()).isEqualTo("user-123");
            assertThat(result.accessToken()).isEqualTo("access-token");
            assertThat(result.refreshToken()).isEqualTo("refresh-token");
            assertThat(result.expiresIn()).isEqualTo(3600L);
            assertThat(result.tokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("내부 로그인 실패 결과를 생성한다")
        void toAuthHubLoginFailure_Success() {
            // given
            String errorCode = "UNAUTHORIZED";
            String errorMessage = "Invalid credentials";

            // when
            AuthHubLoginResult result = mapper.toAuthHubLoginFailure(errorCode, errorMessage);

            // then
            assertThat(result.isFailure()).isTrue();
            assertThat(result.errorCode()).isEqualTo(errorCode);
            assertThat(result.errorMessage()).isEqualTo(errorMessage);
        }

        @Test
        @DisplayName("SDK 토큰 응답을 AuthHubRefreshResult로 변환한다")
        void toAuthHubRefreshResult_Success() {
            // given
            TokenResponse response = mock(TokenResponse.class);
            given(response.accessToken()).willReturn("new-access-token");
            given(response.refreshToken()).willReturn("new-refresh-token");
            given(response.accessTokenExpiresIn()).willReturn(3600L);
            given(response.refreshTokenExpiresIn()).willReturn(86400L);

            // when
            AuthHubRefreshResult result = mapper.toAuthHubRefreshResult(response);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.accessToken()).isEqualTo("new-access-token");
            assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
            assertThat(result.accessTokenExpiresIn()).isEqualTo(3600L);
            assertThat(result.refreshTokenExpiresIn()).isEqualTo(86400L);
        }

        @Test
        @DisplayName("내부 리프레시 실패 결과를 생성한다")
        void toAuthHubRefreshFailure_Success() {
            // given
            String errorCode = "UNAUTHORIZED";
            String errorMessage = "Token expired";

            // when
            AuthHubRefreshResult result = mapper.toAuthHubRefreshFailure(errorCode, errorMessage);

            // then
            assertThat(result.isFailure()).isTrue();
            assertThat(result.errorCode()).isEqualTo(errorCode);
            assertThat(result.errorMessage()).isEqualTo(errorMessage);
        }

        @Test
        @DisplayName("SDK 사용자 정보를 AuthHubUserContext로 변환한다")
        void toAuthHubUserContext_Success() {
            // given
            MyContextResponse response = mock(MyContextResponse.class);
            given(response.userId()).willReturn("user-123");

            // when
            AuthHubUserContext result = mapper.toAuthHubUserContext(response);

            // then
            assertThat(result).isNotNull();
        }
    }
}
