package com.ryuqq.marketplace.adapter.out.persistence.redis.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

/**
 * Redis 통합 테스트 지원 추상 클래스
 *
 * <p>모든 Redis 관련 통합 테스트는 이 클래스를 상속받아 작성합니다. Cache Adapter와 Lock Adapter 테스트 모두 사용 가능합니다.
 *
 * <p>제공 기능:
 *
 * <ul>
 *   <li>TestContainers Redis 자동 설정
 *   <li>RedisTemplate 자동 주입
 *   <li>테스트 후 데이터 자동 정리
 *   <li>기본 검증 유틸리티
 * </ul>
 *
 * <h2>사용 예시:</h2>
 *
 * <pre>{@code
 * @DisplayName("ObjectCacheAdapter 통합 테스트")
 * class ObjectCacheAdapterTest extends RedisTestSupport {
 *
 *     @Autowired
 *     private ObjectCacheAdapter cacheAdapter;
 *
 *     @Test
 *     @DisplayName("성공 - 캐시 저장 및 조회")
 *     void setAndGet_success() {
 *         // Given
 *         OrderCacheKey key = new OrderCacheKey(100L);
 *         Order order = createTestOrder();
 *
 *         // When
 *         cacheAdapter.set(key, order, Duration.ofMinutes(10));
 *         Optional<Order> result = cacheAdapter.get(key, Order.class);
 *
 *         // Then
 *         assertThat(result).isPresent();
 *         assertKeyExists(key.value());
 *     }
 * }
 * }</pre>
 *
 * @author Development Team
 * @since 1.0.0
 * @see CacheTestSupport Cache 전용 테스트 지원
 * @see LockTestSupport Lock 전용 테스트 지원
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class RedisTestSupport {

    private static final Logger log = LoggerFactory.getLogger(RedisTestSupport.class);

    private static final boolean CI_ENVIRONMENT =
            System.getenv("CI") != null || System.getenv("GITHUB_ACTIONS") != null;

    /**
     * Redis TestContainer
     *
     * <p>로컬 환경에서만 사용됩니다. CI 환경에서는 서비스 컨테이너를 사용합니다.
     */
    protected static GenericContainer<?> redis;

    static {
        if (!CI_ENVIRONMENT) {
            redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);
            redis.start();
            log.info("Testcontainers Redis started on port {}", redis.getMappedPort(6379));
        } else {
            log.info("CI environment detected, using service container Redis at localhost:6379");
        }
    }

    /**
     * RedisTemplate - Redis 작업용
     *
     * <p>테스트에서 직접적인 Redis 조작이 필요할 때 사용합니다.
     */
    @Autowired protected RedisTemplate<String, Object> redisTemplate;

    /**
     * 동적 프로퍼티 설정
     *
     * <p>로컬: Testcontainers 동적 포트 사용, CI: localhost:6379 사용
     *
     * @param registry 동적 프로퍼티 레지스트리
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (CI_ENVIRONMENT) {
            registry.add("spring.data.redis.host", () -> "localhost");
            registry.add("spring.data.redis.port", () -> 6379);
        } else {
            registry.add("spring.data.redis.host", redis::getHost);
            registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        }
    }

    /**
     * 테스트 후 Redis 데이터 정리
     *
     * <p>각 테스트 종료 후 Redis의 모든 데이터를 삭제합니다. 테스트 간 데이터 격리를 보장합니다.
     */
    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    /**
     * 키가 존재하는지 검증
     *
     * @param key Redis 키
     */
    protected void assertKeyExists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        assertThat(exists).as("Key '%s' should exist in Redis", key).isTrue();
    }

    /**
     * 키가 존재하지 않는지 검증
     *
     * @param key Redis 키
     */
    protected void assertKeyNotExists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        assertThat(exists).as("Key '%s' should not exist in Redis", key).isFalse();
    }

    /**
     * TTL 남은 시간 조회
     *
     * @param key Redis 키
     * @return TTL (초), 키가 없으면 -2, TTL 없으면 -1
     */
    protected Long getTtl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * Redis에 직접 값 저장
     *
     * @param key Redis 키
     * @param value 저장할 값
     */
    protected void setDirectly(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Redis에서 직접 값 조회
     *
     * @param key Redis 키
     * @return 저장된 값 (없으면 null)
     */
    protected Object getDirectly(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Redis에서 직접 키 삭제
     *
     * @param key Redis 키
     * @return 삭제 성공 여부
     */
    protected Boolean deleteDirectly(String key) {
        return redisTemplate.delete(key);
    }
}
