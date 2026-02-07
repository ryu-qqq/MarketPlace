package com.ryuqq.marketplace.adapter.in.rest.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.in.rest.auth.AuthApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.marketplace.application.auth.dto.command.LoginCommand;
import com.ryuqq.marketplace.application.auth.dto.command.LogoutCommand;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AuthCommandApiMapper 단위 테스트")
class AuthCommandApiMapperTest {

    private AuthCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AuthCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(LoginApiRequest) - 로그인 요청 변환")
    class ToLoginCommandTest {

        @Test
        @DisplayName("LoginApiRequest를 LoginCommand로 변환한다")
        void toCommand_ConvertsLoginRequest_ReturnsCommand() {
            // given
            LoginApiRequest request = AuthApiFixtures.loginRequest();

            // when
            LoginCommand command = mapper.toCommand(request);

            // then
            assertThat(command.identifier()).isEqualTo(AuthApiFixtures.DEFAULT_IDENTIFIER);
            assertThat(command.password()).isEqualTo(AuthApiFixtures.DEFAULT_PASSWORD);
        }

        @Test
        @DisplayName("커스텀 값으로 로그인 요청을 변환한다")
        void toCommand_CustomValues_ReturnsCommandWithCustomValues() {
            // given
            LoginApiRequest request = AuthApiFixtures.loginRequest("user@test.com", "myPassword!");

            // when
            LoginCommand command = mapper.toCommand(request);

            // then
            assertThat(command.identifier()).isEqualTo("user@test.com");
            assertThat(command.password()).isEqualTo("myPassword!");
        }
    }

    @Nested
    @DisplayName("toCommand(String) - 로그아웃 요청 변환")
    class ToLogoutCommandTest {

        @Test
        @DisplayName("userId를 LogoutCommand로 변환한다")
        void toCommand_ConvertsUserId_ReturnsLogoutCommand() {
            // given
            String userId = "user-123";

            // when
            LogoutCommand command = mapper.toCommand(userId);

            // then
            assertThat(command.userId()).isEqualTo("user-123");
        }
    }

    @Nested
    @DisplayName("toResponse(LoginResult) - 로그인 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("성공 LoginResult를 LoginApiResponse로 변환한다")
        void toResponse_SuccessResult_ReturnsLoginApiResponse() {
            // given
            LoginResult result = AuthApiFixtures.successLoginResult();

            // when
            LoginApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.accessToken()).isEqualTo(AuthApiFixtures.DEFAULT_ACCESS_TOKEN);
            assertThat(response.refreshToken()).isEqualTo(AuthApiFixtures.DEFAULT_REFRESH_TOKEN);
            assertThat(response.tokenType()).isEqualTo(AuthApiFixtures.DEFAULT_TOKEN_TYPE);
            assertThat(response.expiresIn()).isEqualTo(AuthApiFixtures.DEFAULT_EXPIRES_IN);
        }

        @Test
        @DisplayName("실패 LoginResult는 IllegalArgumentException을 발생시킨다")
        void toResponse_FailureResult_ThrowsIllegalArgumentException() {
            // given
            LoginResult result = AuthApiFixtures.failureLoginResult();

            // when & then
            assertThatThrownBy(() -> mapper.toResponse(result))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid credentials");
        }
    }
}
