package com.ryuqq.marketplace.application.legacy.auth.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.auth.LegacyAuthFixtures;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenCacheQueryPort;
import java.util.Optional;
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
@DisplayName("LegacyTokenCacheReadManager 단위 테스트")
class LegacyTokenCacheReadManagerTest {

    @InjectMocks private LegacyTokenCacheReadManager sut;

    @Mock private LegacyTokenCacheQueryPort cacheQueryPort;

    @Nested
    @DisplayName("findByEmail() - 이메일로 리프레시 토큰 조회")
    class FindByEmailTest {

        @Test
        @DisplayName("캐시에 리프레시 토큰이 존재하면 Optional로 반환한다")
        void findByEmail_TokenExists_ReturnsOptionalToken() {
            // given
            String email = LegacyAuthFixtures.DEFAULT_EMAIL;
            String expectedToken = LegacyAuthFixtures.DEFAULT_REFRESH_TOKEN;

            given(cacheQueryPort.findByEmail(email)).willReturn(Optional.of(expectedToken));

            // when
            Optional<String> result = sut.findByEmail(email);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedToken);
            then(cacheQueryPort).should().findByEmail(email);
        }

        @Test
        @DisplayName("캐시에 리프레시 토큰이 없으면 Optional.empty()를 반환한다")
        void findByEmail_TokenNotExists_ReturnsEmpty() {
            // given
            String email = "notcached@test.com";

            given(cacheQueryPort.findByEmail(email)).willReturn(Optional.empty());

            // when
            Optional<String> result = sut.findByEmail(email);

            // then
            assertThat(result).isEmpty();
            then(cacheQueryPort).should().findByEmail(email);
        }
    }
}
