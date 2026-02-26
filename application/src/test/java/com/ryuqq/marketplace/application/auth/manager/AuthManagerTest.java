package com.ryuqq.marketplace.application.auth.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.auth.AuthResultFixtures;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.port.out.client.AuthClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * AuthManager 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthManager 단위 테스트")
class AuthManagerTest {

    @InjectMocks private AuthManager sut;

    @Mock private AuthClient authClient;

    @Nested
    @DisplayName("login() - 로그인")
    class LoginTest {

        @Test
        @DisplayName("로그인을 수행하고 결과를 반환한다")
        void login_Success() {
            // given
            String identifier = "admin@example.com";
            String password = "password123!";
            LoginResult expectedResult = AuthResultFixtures.successLoginResult();

            given(authClient.login(identifier, password)).willReturn(expectedResult);

            // when
            LoginResult result = sut.login(identifier, password);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(authClient).should().login(identifier, password);
        }

        @Test
        @DisplayName("로그인 실패 시 실패 결과를 반환한다")
        void login_Failed_ReturnsFailure() {
            // given
            String identifier = "admin@example.com";
            String password = "wrong-password";
            LoginResult expectedResult = AuthResultFixtures.failureLoginResult();

            given(authClient.login(identifier, password)).willReturn(expectedResult);

            // when
            LoginResult result = sut.login(identifier, password);

            // then
            assertThat(result.isFailure()).isTrue();
            then(authClient).should().login(identifier, password);
        }
    }

    @Nested
    @DisplayName("logout() - 로그아웃")
    class LogoutTest {

        @Test
        @DisplayName("로그아웃을 수행한다")
        void logout_Success() {
            // given
            String userId = "user-123";
            willDoNothing().given(authClient).logout(userId);

            // when
            sut.logout(userId);

            // then
            then(authClient).should().logout(userId);
        }
    }

    @Nested
    @DisplayName("getMyInfo() - 내 정보 조회")
    class GetMyInfoTest {

        @Test
        @DisplayName("내 정보를 조회한다")
        void getMyInfo_Success() {
            // given
            String accessToken = "test-access-token";
            MyInfoResult expectedResult = AuthResultFixtures.myInfoResult();

            given(authClient.getMyInfo(accessToken)).willReturn(expectedResult);

            // when
            MyInfoResult result = sut.getMyInfo(accessToken);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(authClient).should().getMyInfo(accessToken);
        }
    }
}
