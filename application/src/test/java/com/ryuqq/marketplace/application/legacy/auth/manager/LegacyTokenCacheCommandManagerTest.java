package com.ryuqq.marketplace.application.legacy.auth.manager;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

import com.ryuqq.marketplace.application.legacy.auth.LegacyAuthFixtures;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenCacheCommandPort;
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
@DisplayName("LegacyTokenCacheCommandManager 단위 테스트")
class LegacyTokenCacheCommandManagerTest {

    @InjectMocks private LegacyTokenCacheCommandManager sut;

    @Mock private LegacyTokenCacheCommandPort cacheCommandPort;

    @Nested
    @DisplayName("persist() - 리프레시 토큰 캐시 저장")
    class PersistTest {

        @Test
        @DisplayName("이메일, 리프레시 토큰, TTL을 캐시에 저장한다")
        void persist_ValidParams_DelegatesToPort() {
            // given
            String email = LegacyAuthFixtures.DEFAULT_EMAIL;
            String refreshToken = LegacyAuthFixtures.DEFAULT_REFRESH_TOKEN;
            long expiresInSeconds = LegacyAuthFixtures.DEFAULT_EXPIRES_IN_SECONDS;

            doNothing().when(cacheCommandPort).persist(email, refreshToken, expiresInSeconds);

            // when
            sut.persist(email, refreshToken, expiresInSeconds);

            // then
            then(cacheCommandPort).should().persist(email, refreshToken, expiresInSeconds);
        }

        @Test
        @DisplayName("TTL이 다른 경우에도 포트에 위임한다")
        void persist_DifferentTtl_DelegatesToPort() {
            // given
            String email = LegacyAuthFixtures.DEFAULT_EMAIL;
            String refreshToken = LegacyAuthFixtures.DEFAULT_REFRESH_TOKEN;
            long shortTtl = 3600L;

            doNothing().when(cacheCommandPort).persist(email, refreshToken, shortTtl);

            // when
            sut.persist(email, refreshToken, shortTtl);

            // then
            then(cacheCommandPort).should().persist(email, refreshToken, shortTtl);
        }
    }
}
