package com.ryuqq.marketplace.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.auth.AuthCommandFixtures;
import com.ryuqq.marketplace.application.auth.AuthResultFixtures;
import com.ryuqq.marketplace.application.auth.dto.command.LoginCommand;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.manager.AuthManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LoginService 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class LoginServiceTest {

    @InjectMocks private LoginService sut;

    @Mock private AuthManager authManager;

    @Nested
    @DisplayName("execute() - 로그인")
    class ExecuteTest {

        @Test
        @DisplayName("로그인을 수행하고 결과를 반환한다")
        void execute_Login_ReturnsResult() {
            // given
            LoginCommand command = AuthCommandFixtures.loginCommand();
            LoginResult expectedResult = AuthResultFixtures.successLoginResult();

            given(authManager.login(command.identifier(), command.password()))
                    .willReturn(expectedResult);

            // when
            LoginResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.isSuccess()).isTrue();
            then(authManager).should().login(command.identifier(), command.password());
        }

        @Test
        @DisplayName("로그인 실패 시 실패 결과를 반환한다")
        void execute_LoginFailed_ReturnsFailureResult() {
            // given
            LoginCommand command = AuthCommandFixtures.loginCommand();
            LoginResult expectedResult = AuthResultFixtures.failureLoginResult();

            given(authManager.login(command.identifier(), command.password()))
                    .willReturn(expectedResult);

            // when
            LoginResult result = sut.execute(command);

            // then
            assertThat(result.isFailure()).isTrue();
            assertThat(result.errorCode()).isEqualTo("UNAUTHORIZED");
        }
    }
}
