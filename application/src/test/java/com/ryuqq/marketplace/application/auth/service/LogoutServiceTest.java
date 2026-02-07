package com.ryuqq.marketplace.application.auth.service;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.auth.AuthCommandFixtures;
import com.ryuqq.marketplace.application.auth.dto.command.LogoutCommand;
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
 * LogoutService 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LogoutService 단위 테스트")
class LogoutServiceTest {

    @InjectMocks private LogoutService sut;

    @Mock private AuthManager authManager;

    @Nested
    @DisplayName("execute() - 로그아웃")
    class ExecuteTest {

        @Test
        @DisplayName("로그아웃을 수행한다")
        void execute_Logout_Success() {
            // given
            LogoutCommand command = AuthCommandFixtures.logoutCommand();
            willDoNothing().given(authManager).logout(command.userId());

            // when
            sut.execute(command);

            // then
            then(authManager).should().logout(command.userId());
        }
    }
}
