package com.ryuqq.marketplace.adapter.out.persistence.redis.cache.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.redis.common.CacheTestSupport;
import com.ryuqq.marketplace.adapter.out.persistence.redis.common.config.LettuceConfig;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * LegacyRefreshTokenCacheAdapter 통합 테스트.
 *
 * <p>TestContainers Redis를 사용하여 실제 Redis 동작을 검증합니다.
 *
 * <p>테스트 시나리오:
 *
 * <ul>
 *   <li>persist: 토큰이 올바른 키로 저장되고 TTL이 적용되는지 검증
 *   <li>findByEmail: 저장된 토큰 조회, 미저장 이메일 조회, persist 후 즉시 조회 흐름 검증
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@DisplayName("LegacyRefreshTokenCacheAdapter 통합 테스트")
@SpringBootTest(classes = {LettuceConfig.class, LegacyRefreshTokenCacheAdapter.class})
class LegacyRefreshTokenCacheAdapterIntegrationTest extends CacheTestSupport {

    private static final String KEY_PREFIX = "refresh_token:";
    private static final String TEST_EMAIL = "seller@example.com";
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.testtoken";
    private static final long EXPIRES_IN_SECONDS = 604800L; // 7일

    @Autowired private LegacyRefreshTokenCacheAdapter adapter;

    // ========================================================================
    // 1. persist 통합 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("성공 - 토큰이 올바른 키로 Redis에 저장됩니다")
        void persist_WithValidInput_StoresTokenWithCorrectKey() {
            // when
            adapter.persist(TEST_EMAIL, TEST_TOKEN, EXPIRES_IN_SECONDS);

            // then
            String expectedKey = KEY_PREFIX + TEST_EMAIL;
            assertCacheExists(expectedKey);
            assertCacheValueEquals(expectedKey, TEST_TOKEN);
        }

        @Test
        @DisplayName("성공 - TTL이 설정됩니다")
        void persist_WithTtl_SetsTtlOnKey() {
            // when
            adapter.persist(TEST_EMAIL, TEST_TOKEN, EXPIRES_IN_SECONDS);

            // then
            String expectedKey = KEY_PREFIX + TEST_EMAIL;
            assertTtlSet(expectedKey, EXPIRES_IN_SECONDS, 10L);
        }

        @Test
        @DisplayName("성공 - 동일 이메일로 재저장 시 덮어씁니다")
        void persist_WithSameEmail_OverwritesPreviousToken() {
            // given
            String firstToken = "first.token.value";
            String secondToken = "second.token.value";
            adapter.persist(TEST_EMAIL, firstToken, EXPIRES_IN_SECONDS);

            // when
            adapter.persist(TEST_EMAIL, secondToken, EXPIRES_IN_SECONDS);

            // then
            String expectedKey = KEY_PREFIX + TEST_EMAIL;
            assertCacheValueEquals(expectedKey, secondToken);
        }

        @Test
        @DisplayName("성공 - 서로 다른 이메일은 독립된 키로 저장됩니다")
        void persist_WithDifferentEmails_StoresAsIndependentKeys() {
            // given
            String emailA = "sellerA@example.com";
            String emailB = "sellerB@example.com";
            String tokenA = "token.for.seller.a";
            String tokenB = "token.for.seller.b";

            // when
            adapter.persist(emailA, tokenA, EXPIRES_IN_SECONDS);
            adapter.persist(emailB, tokenB, EXPIRES_IN_SECONDS);

            // then
            assertCacheValueEquals(KEY_PREFIX + emailA, tokenA);
            assertCacheValueEquals(KEY_PREFIX + emailB, tokenB);
        }

        @Test
        @DisplayName("성공 - 짧은 TTL로 저장 후 키가 존재합니다")
        void persist_WithShortTtl_KeyExistsImmediately() {
            // when
            adapter.persist(TEST_EMAIL, TEST_TOKEN, 5L);

            // then
            assertCacheExists(KEY_PREFIX + TEST_EMAIL);
            assertTtlSet(KEY_PREFIX + TEST_EMAIL, 5L, 2L);
        }
    }

    // ========================================================================
    // 2. findByEmail 통합 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByEmail 메서드")
    class FindByEmailTest {

        @Test
        @DisplayName("성공 - 저장된 토큰을 이메일로 조회합니다")
        void findByEmail_WhenTokenExists_ReturnsToken() {
            // given
            adapter.persist(TEST_EMAIL, TEST_TOKEN, EXPIRES_IN_SECONDS);

            // when
            Optional<String> result = adapter.findByEmail(TEST_EMAIL);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(TEST_TOKEN);
        }

        @Test
        @DisplayName("성공 - 저장되지 않은 이메일 조회 시 Optional.empty를 반환합니다")
        void findByEmail_WhenTokenNotExists_ReturnsEmpty() {
            // given - 저장 없음

            // when
            Optional<String> result = adapter.findByEmail("unknown@example.com");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공 - persist 직후 findByEmail로 즉시 조회됩니다")
        void findByEmail_AfterPersist_ReturnsStoredToken() {
            // given
            String email = "fresh@example.com";
            String token = "fresh.token.value";

            // when
            adapter.persist(email, token, EXPIRES_IN_SECONDS);
            Optional<String> result = adapter.findByEmail(email);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(token);
        }

        @Test
        @DisplayName("성공 - 재저장 후 findByEmail은 최신 토큰을 반환합니다")
        void findByEmail_AfterOverwrite_ReturnsLatestToken() {
            // given
            String oldToken = "old.token.value";
            String newToken = "new.token.value";
            adapter.persist(TEST_EMAIL, oldToken, EXPIRES_IN_SECONDS);

            // when
            adapter.persist(TEST_EMAIL, newToken, EXPIRES_IN_SECONDS);
            Optional<String> result = adapter.findByEmail(TEST_EMAIL);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(newToken);
        }

        @Test
        @DisplayName("성공 - 서로 다른 이메일은 각각의 토큰을 반환합니다")
        void findByEmail_WithMultipleEmails_ReturnsCorrectToken() {
            // given
            String emailA = "sellerA@example.com";
            String emailB = "sellerB@example.com";
            String tokenA = "token.A";
            String tokenB = "token.B";
            adapter.persist(emailA, tokenA, EXPIRES_IN_SECONDS);
            adapter.persist(emailB, tokenB, EXPIRES_IN_SECONDS);

            // when
            Optional<String> resultA = adapter.findByEmail(emailA);
            Optional<String> resultB = adapter.findByEmail(emailB);

            // then
            assertThat(resultA).isPresent();
            assertThat(resultA.get()).isEqualTo(tokenA);
            assertThat(resultB).isPresent();
            assertThat(resultB.get()).isEqualTo(tokenB);
        }
    }
}
