package com.ryuqq.marketplace.adapter.out.persistence.redis.cache.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * LegacyRefreshTokenCacheAdapter 단위 테스트.
 *
 * <p>RedisTemplate을 Mock으로 처리하여 LegacyRefreshTokenCacheAdapter의 동작을 검증합니다.
 *
 * <p>검증 항목:
 *
 * <ul>
 *   <li>persist: 키 포맷("refresh_token:{email}"), TTL 설정, RedisTemplate 호출
 *   <li>findByEmail: 값이 존재하는 경우 Optional.of 반환, 값이 null인 경우 Optional.empty 반환
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyRefreshTokenCacheAdapter 단위 테스트")
class LegacyRefreshTokenCacheAdapterTest {

    private static final String KEY_PREFIX = "refresh_token:";
    private static final String TEST_EMAIL = "seller@example.com";
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.testtoken";
    private static final long EXPIRES_IN_SECONDS = 604800L; // 7일

    @Mock private RedisTemplate<String, Object> redisTemplate;

    @Mock private ValueOperations<String, Object> valueOperations;

    @InjectMocks private LegacyRefreshTokenCacheAdapter adapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @BeforeEach
        void setUp() {
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
        }

        @Test
        @DisplayName("이메일과 토큰, TTL을 전달하면 Redis에 저장합니다")
        void persist_WithValidInput_StoresTokenInRedis() {
            // when
            adapter.persist(TEST_EMAIL, TEST_TOKEN, EXPIRES_IN_SECONDS);

            // then
            then(valueOperations)
                    .should()
                    .set(KEY_PREFIX + TEST_EMAIL, TEST_TOKEN, Duration.ofSeconds(EXPIRES_IN_SECONDS));
        }

        @Test
        @DisplayName("키는 'refresh_token:{email}' 포맷으로 구성됩니다")
        void persist_KeyFormat_IsRefreshTokenColonEmail() {
            // given
            String email = "admin@shop.com";
            String expectedKey = "refresh_token:admin@shop.com";

            // when
            adapter.persist(email, TEST_TOKEN, EXPIRES_IN_SECONDS);

            // then
            then(valueOperations)
                    .should()
                    .set(expectedKey, TEST_TOKEN, Duration.ofSeconds(EXPIRES_IN_SECONDS));
        }

        @Test
        @DisplayName("TTL은 Duration.ofSeconds로 변환되어 전달됩니다")
        void persist_Ttl_IsConvertedToDuration() {
            // given
            long expiresInSeconds = 3600L; // 1시간

            // when
            adapter.persist(TEST_EMAIL, TEST_TOKEN, expiresInSeconds);

            // then
            then(valueOperations)
                    .should()
                    .set(KEY_PREFIX + TEST_EMAIL, TEST_TOKEN, Duration.ofSeconds(3600L));
        }

        @Test
        @DisplayName("opsForValue()가 한 번 호출됩니다")
        void persist_CallsOpsForValueOnce() {
            // when
            adapter.persist(TEST_EMAIL, TEST_TOKEN, EXPIRES_IN_SECONDS);

            // then
            then(redisTemplate).should().opsForValue();
        }
    }

    // ========================================================================
    // 2. findByEmail 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByEmail 메서드 테스트")
    class FindByEmailTest {

        @BeforeEach
        void setUp() {
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
        }

        @Test
        @DisplayName("Redis에 값이 존재하면 Optional.of로 토큰을 반환합니다")
        void findByEmail_WhenTokenExists_ReturnsOptionalOfToken() {
            // given
            given(valueOperations.get(KEY_PREFIX + TEST_EMAIL)).willReturn(TEST_TOKEN);

            // when
            Optional<String> result = adapter.findByEmail(TEST_EMAIL);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(TEST_TOKEN);
            then(valueOperations).should().get(KEY_PREFIX + TEST_EMAIL);
        }

        @Test
        @DisplayName("Redis에 값이 없으면 Optional.empty를 반환합니다")
        void findByEmail_WhenTokenNotExists_ReturnsOptionalEmpty() {
            // given
            given(valueOperations.get(KEY_PREFIX + TEST_EMAIL)).willReturn(null);

            // when
            Optional<String> result = adapter.findByEmail(TEST_EMAIL);

            // then
            assertThat(result).isEmpty();
            then(valueOperations).should().get(KEY_PREFIX + TEST_EMAIL);
        }

        @Test
        @DisplayName("키는 'refresh_token:{email}' 포맷으로 조회합니다")
        void findByEmail_KeyFormat_IsRefreshTokenColonEmail() {
            // given
            String email = "manager@store.com";
            String expectedKey = "refresh_token:manager@store.com";
            given(valueOperations.get(expectedKey)).willReturn(TEST_TOKEN);

            // when
            adapter.findByEmail(email);

            // then
            then(valueOperations).should().get(expectedKey);
        }

        @Test
        @DisplayName("Object 타입으로 저장된 값을 toString()으로 변환하여 반환합니다")
        void findByEmail_WhenValueIsObject_ReturnsToStringConversion() {
            // given
            Object storedValue = new StringBuilder(TEST_TOKEN);
            given(valueOperations.get(KEY_PREFIX + TEST_EMAIL)).willReturn(storedValue);

            // when
            Optional<String> result = adapter.findByEmail(TEST_EMAIL);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(TEST_TOKEN);
        }

        @Test
        @DisplayName("opsForValue()가 한 번 호출됩니다")
        void findByEmail_CallsOpsForValueOnce() {
            // given
            given(valueOperations.get(KEY_PREFIX + TEST_EMAIL)).willReturn(TEST_TOKEN);

            // when
            adapter.findByEmail(TEST_EMAIL);

            // then
            then(redisTemplate).should().opsForValue();
        }
    }

    // ========================================================================
    // 3. Port 구현 검증
    // ========================================================================

    @Nested
    @DisplayName("Port 구현 검증")
    class PortImplementationTest {

        @Test
        @DisplayName("LegacyTokenCacheCommandPort를 구현합니다")
        void adapter_ImplementsLegacyTokenCacheCommandPort() {
            assertThat(adapter)
                    .isInstanceOf(
                            com.ryuqq.marketplace.application.legacy.auth.port.out
                                    .LegacyTokenCacheCommandPort.class);
        }

        @Test
        @DisplayName("LegacyTokenCacheQueryPort를 구현합니다")
        void adapter_ImplementsLegacyTokenCacheQueryPort() {
            assertThat(adapter)
                    .isInstanceOf(
                            com.ryuqq.marketplace.application.legacy.auth.port.out
                                    .LegacyTokenCacheQueryPort.class);
        }
    }
}
