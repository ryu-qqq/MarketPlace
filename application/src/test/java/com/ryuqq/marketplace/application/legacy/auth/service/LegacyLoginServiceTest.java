package com.ryuqq.marketplace.application.legacy.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.auth.LegacyAuthFixtures;
import com.ryuqq.marketplace.application.legacy.auth.dto.command.LegacyLoginCommand;
import com.ryuqq.marketplace.application.legacy.auth.internal.LegacyLoginCoordinator;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminInvalidPasswordException;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotFoundException;
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
@DisplayName("LegacyLoginService 단위 테스트")
class LegacyLoginServiceTest {

    @InjectMocks private LegacyLoginService sut;

    @Mock private LegacyLoginCoordinator legacyLoginCoordinator;

    @Nested
    @DisplayName("execute() - 레거시 로그인 실행")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 로그인하면 액세스 토큰을 반환한다")
        void execute_ValidCommand_ReturnsAccessToken() {
            // given
            LegacyLoginCommand command = LegacyAuthFixtures.loginCommand();
            String expectedAccessToken = LegacyAuthFixtures.DEFAULT_ACCESS_TOKEN;

            given(legacyLoginCoordinator.login(command.identifier(), command.password()))
                    .willReturn(expectedAccessToken);

            // when
            String result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedAccessToken);
            then(legacyLoginCoordinator).should().login(command.identifier(), command.password());
        }

        @Test
        @DisplayName("셀러가 존재하지 않으면 SellerAdminNotFoundException이 전파된다")
        void execute_SellerNotFound_PropagatesException() {
            // given
            LegacyLoginCommand command = LegacyAuthFixtures.loginCommand();

            given(legacyLoginCoordinator.login(command.identifier(), command.password()))
                    .willThrow(SellerAdminNotFoundException.withMessage("미발견"));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(SellerAdminNotFoundException.class);
            then(legacyLoginCoordinator).should().login(command.identifier(), command.password());
        }

        @Test
        @DisplayName("비밀번호가 틀리면 SellerAdminInvalidPasswordException이 전파된다")
        void execute_InvalidPassword_PropagatesException() {
            // given
            LegacyLoginCommand command = LegacyAuthFixtures.loginCommand();

            given(legacyLoginCoordinator.login(command.identifier(), command.password()))
                    .willThrow(new SellerAdminInvalidPasswordException());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(SellerAdminInvalidPasswordException.class);
            then(legacyLoginCoordinator).should().login(command.identifier(), command.password());
        }

        @Test
        @DisplayName("커맨드의 identifier와 password가 Coordinator에 그대로 전달된다")
        void execute_CommandValues_PassedToCoordinator() {
            // given
            String identifier = "custom@example.com";
            String password = "customPassword!";
            LegacyLoginCommand command = LegacyAuthFixtures.loginCommand(identifier, password);
            String expectedToken = "custom.access.token";

            given(legacyLoginCoordinator.login(identifier, password)).willReturn(expectedToken);

            // when
            String result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedToken);
            then(legacyLoginCoordinator).should().login(identifier, password);
        }
    }
}
