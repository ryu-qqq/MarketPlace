package com.ryuqq.marketplace.application.legacy.auth.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.port.out.client.AuthClient;
import com.ryuqq.marketplace.application.legacy.auth.facade.LegacyTokenIssuanceFacade;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminInvalidPasswordException;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyLoginCoordinator 테스트")
class LegacyLoginCoordinatorTest {

    @Mock private AuthClient authClient;
    @Mock private SellerAdminReadManager sellerAdminReadManager;
    @Mock private LegacyTokenIssuanceFacade tokenIssuanceFacade;

    @InjectMocks private LegacyLoginCoordinator coordinator;

    private static final String EMAIL = "seller@test.com";
    private static final String PASSWORD = "password123";
    private static final String USER_ID = "user-uuid-001";
    private static final String ACCESS_TOKEN = "access.jwt.token";

    @Nested
    @DisplayName("login")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공 — AuthHub 인증 후 자체 JWT 발급 (SELLER)")
        void login_Success() {
            String authToken = buildFakeJwt("{\"roles\":[\"ADMIN\"]}");
            LoginResult loginResult =
                    LoginResult.success(USER_ID, authToken, "refresh", 1800L, "Bearer");

            SellerAdmin sellerAdmin = org.mockito.Mockito.mock(SellerAdmin.class);
            given(sellerAdmin.sellerIdValue()).willReturn(10L);
            given(authClient.login(EMAIL, PASSWORD)).willReturn(loginResult);
            given(sellerAdminReadManager.getByAuthUserId(USER_ID)).willReturn(sellerAdmin);
            given(tokenIssuanceFacade.issueAndCache(EMAIL, 10L, "SELLER")).willReturn(ACCESS_TOKEN);

            String result = coordinator.login(EMAIL, PASSWORD);

            assertThat(result).isEqualTo(ACCESS_TOKEN);
            verify(tokenIssuanceFacade).issueAndCache(EMAIL, 10L, "SELLER");
        }

        @Test
        @DisplayName("마스터 로그인 — SUPER_ADMIN role이면 MASTER로 변환")
        void login_Master() {
            String authToken = buildFakeJwt("{\"roles\":[\"SUPER_ADMIN\"]}");
            LoginResult loginResult =
                    LoginResult.success(USER_ID, authToken, "refresh", 1800L, "Bearer");

            SellerAdmin sellerAdmin = org.mockito.Mockito.mock(SellerAdmin.class);
            given(sellerAdmin.sellerIdValue()).willReturn(1L);
            given(authClient.login(EMAIL, PASSWORD)).willReturn(loginResult);
            given(sellerAdminReadManager.getByAuthUserId(USER_ID)).willReturn(sellerAdmin);
            given(tokenIssuanceFacade.issueAndCache(EMAIL, 1L, "MASTER")).willReturn(ACCESS_TOKEN);

            String result = coordinator.login(EMAIL, PASSWORD);

            assertThat(result).isEqualTo(ACCESS_TOKEN);
            verify(tokenIssuanceFacade).issueAndCache(EMAIL, 1L, "MASTER");
        }

        @Test
        @DisplayName("AuthHub 인증 실패 시 예외")
        void login_AuthFailed() {
            LoginResult failResult = LoginResult.failure("UNAUTHORIZED", "비밀번호 불일치");
            given(authClient.login(EMAIL, PASSWORD)).willReturn(failResult);

            assertThatThrownBy(() -> coordinator.login(EMAIL, PASSWORD))
                    .isInstanceOf(SellerAdminInvalidPasswordException.class);
        }

        @Test
        @DisplayName("AuthHub 예외 발생 시에도 SellerAdminInvalidPasswordException")
        void login_AuthException() {
            given(authClient.login(EMAIL, PASSWORD))
                    .willThrow(new RuntimeException("connection refused"));

            assertThatThrownBy(() -> coordinator.login(EMAIL, PASSWORD))
                    .isInstanceOf(SellerAdminInvalidPasswordException.class);
        }
    }

    /** header.payload.signature 형식의 가짜 JWT 생성. */
    private String buildFakeJwt(String payloadJson) {
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{}".getBytes());
        String payload =
                Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        return header + "." + payload + ".fake-signature";
    }
}
