package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyCreateAuthTokenRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.response.LegacyAuthTokenResponse;
import com.ryuqq.marketplace.application.legacy.auth.dto.command.LegacyLoginCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyAuthCommandApiMapper 단위 테스트")
class LegacyAuthCommandApiMapperTest {

    private LegacyAuthCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyAuthCommandApiMapper();
    }

    @Nested
    @DisplayName("toLoginCommand - 인증 토큰 생성 요청 변환")
    class ToLoginCommandTest {

        @Test
        @DisplayName("LegacyCreateAuthTokenRequest를 LegacyLoginCommand로 변환한다")
        void toLoginCommand_ConvertsRequest_ReturnsCommand() {
            // given
            LegacyCreateAuthTokenRequest request = LegacyAuthApiFixtures.request();

            // when
            LegacyLoginCommand command = mapper.toLoginCommand(request);

            // then
            assertThat(command.identifier()).isEqualTo(LegacyAuthApiFixtures.DEFAULT_USER_ID);
            assertThat(command.password()).isEqualTo(LegacyAuthApiFixtures.DEFAULT_PASSWORD);
        }

        @Test
        @DisplayName("userId가 Command의 identifier로 올바르게 매핑된다")
        void toLoginCommand_MapsUserIdToIdentifier_Correctly() {
            // given
            LegacyCreateAuthTokenRequest request =
                    LegacyAuthApiFixtures.requestWith("customUser", "customPass", "SELLER");

            // when
            LegacyLoginCommand command = mapper.toLoginCommand(request);

            // then
            assertThat(command.identifier()).isEqualTo("customUser");
            assertThat(command.password()).isEqualTo("customPass");
        }
    }

    @Nested
    @DisplayName("toAuthTokenResponse - 토큰 문자열을 응답 DTO로 변환")
    class ToAuthTokenResponseTest {

        @Test
        @DisplayName("토큰 문자열을 LegacyAuthTokenResponse로 변환한다")
        void toAuthTokenResponse_ConvertsToken_ReturnsResponse() {
            // given
            String token = LegacyAuthApiFixtures.DEFAULT_TOKEN;

            // when
            LegacyAuthTokenResponse response = mapper.toAuthTokenResponse(token);

            // then
            assertThat(response.token()).isEqualTo(token);
        }

        @Test
        @DisplayName("다른 토큰 값으로도 올바르게 변환된다")
        void toAuthTokenResponse_WithDifferentToken_ReturnsCorrectResponse() {
            // given
            String token = "another.jwt.token";

            // when
            LegacyAuthTokenResponse response = mapper.toAuthTokenResponse(token);

            // then
            assertThat(response.token()).isEqualTo("another.jwt.token");
        }
    }
}
