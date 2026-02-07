package com.ryuqq.marketplace.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.auth.AuthResultFixtures;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
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
 * GetMyInfoService 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetMyInfoService 단위 테스트")
class GetMyInfoServiceTest {

    @InjectMocks private GetMyInfoService sut;

    @Mock private AuthManager authManager;

    @Nested
    @DisplayName("execute() - 내 정보 조회")
    class ExecuteTest {

        @Test
        @DisplayName("내 정보를 조회하고 결과를 반환한다")
        void execute_GetMyInfo_ReturnsResult() {
            // given
            String accessToken = "test-access-token";
            MyInfoResult expectedResult = AuthResultFixtures.myInfoResult();

            given(authManager.getMyInfo(accessToken)).willReturn(expectedResult);

            // when
            MyInfoResult result = sut.execute(accessToken);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.userId()).isEqualTo(AuthResultFixtures.DEFAULT_USER_ID);
            then(authManager).should().getMyInfo(accessToken);
        }
    }
}
