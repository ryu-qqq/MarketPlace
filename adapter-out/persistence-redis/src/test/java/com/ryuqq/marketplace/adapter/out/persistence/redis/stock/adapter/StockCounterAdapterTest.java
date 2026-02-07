package com.ryuqq.marketplace.adapter.out.persistence.redis.stock.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.redis.common.config.LettuceConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * StockCounterAdapter 통합 테스트
 *
 * <p>Redis Lua 스크립트를 사용한 원자적 재고 연산을 검증합니다.
 *
 * <p>테스트 시나리오:
 *
 * <ul>
 *   <li>키 존재 시 decrement/increment 동작
 *   <li>키 미존재 시 NOT_FOUND(-1) 반환
 *   <li>재고 부족 시 음수 반환
 *   <li>배치 연산 동작
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("StockCounterAdapter 통합 테스트")
@SpringBootTest(classes = {LettuceConfig.class, StockCounterAdapter.class})
@ActiveProfiles("test")
@Testcontainers
class StockCounterAdapterTest {

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired private RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "stock:counter:";
    private static final int NOT_FOUND = -1;

    @Autowired private StockCounterAdapter stockCounterAdapter;

    private Long productStockId;

    @BeforeEach
    void setUp() {
        productStockId = 100L;
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    private void assertKeyExists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        assertThat(exists).as("Key '%s' should exist in Redis", key).isTrue();
    }

    private void assertKeyNotExists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        assertThat(exists).as("Key '%s' should not exist in Redis", key).isFalse();
    }

    @Nested
    @DisplayName("decrement 메서드")
    class DecrementTest {

        @Test
        @DisplayName("성공 - 키 존재 시 재고 차감")
        void shouldDecrementWhenKeyExists() {
            // given
            stockCounterAdapter.initialize(productStockId, 10);

            // when
            int remaining = stockCounterAdapter.decrement(productStockId, 3);

            // then
            assertThat(remaining).isEqualTo(7);
            assertThat(stockCounterAdapter.getStock(productStockId)).isEqualTo(7);
        }

        @Test
        @DisplayName("성공 - 키 미존재 시 NOT_FOUND 반환")
        void shouldReturnNotFoundWhenKeyNotExists() {
            // given - 키 없음

            // when
            int result = stockCounterAdapter.decrement(productStockId, 3);

            // then
            assertThat(result).isEqualTo(NOT_FOUND);
            assertKeyNotExists(KEY_PREFIX + productStockId);
        }

        @Test
        @DisplayName("성공 - 재고 부족 시 음수 반환")
        void shouldReturnNegativeWhenInsufficientStock() {
            // given
            stockCounterAdapter.initialize(productStockId, 5);

            // when
            int remaining = stockCounterAdapter.decrement(productStockId, 10);

            // then
            assertThat(remaining).isEqualTo(-5);
        }
    }

    @Nested
    @DisplayName("increment 메서드")
    class IncrementTest {

        @Test
        @DisplayName("성공 - 키 존재 시 재고 증가")
        void shouldIncrementWhenKeyExists() {
            // given
            stockCounterAdapter.initialize(productStockId, 10);

            // when
            int result = stockCounterAdapter.increment(productStockId, 5);

            // then
            assertThat(result).isEqualTo(15);
            assertThat(stockCounterAdapter.getStock(productStockId)).isEqualTo(15);
        }

        @Test
        @DisplayName("성공 - 키 미존재 시 NOT_FOUND 반환")
        void shouldReturnNotFoundWhenKeyNotExists() {
            // given - 키 없음

            // when
            int result = stockCounterAdapter.increment(productStockId, 5);

            // then
            assertThat(result).isEqualTo(NOT_FOUND);
            assertKeyNotExists(KEY_PREFIX + productStockId);
        }

        @Test
        @DisplayName("성공 - 롤백 시나리오 (decrement 후 increment)")
        void shouldSupportRollbackScenario() {
            // given
            stockCounterAdapter.initialize(productStockId, 10);
            int remaining = stockCounterAdapter.decrement(productStockId, 7);
            assertThat(remaining).isEqualTo(3);

            // when - 롤백
            int afterRollback = stockCounterAdapter.increment(productStockId, 7);

            // then
            assertThat(afterRollback).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("getStock 메서드")
    class GetStockTest {

        @Test
        @DisplayName("성공 - 키 존재 시 재고 반환")
        void shouldReturnStockWhenKeyExists() {
            // given
            stockCounterAdapter.initialize(productStockId, 25);

            // when
            int stock = stockCounterAdapter.getStock(productStockId);

            // then
            assertThat(stock).isEqualTo(25);
        }

        @Test
        @DisplayName("성공 - 키 미존재 시 NOT_FOUND 반환")
        void shouldReturnNotFoundWhenKeyNotExists() {
            // when
            int stock = stockCounterAdapter.getStock(productStockId);

            // then
            assertThat(stock).isEqualTo(NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("hasStock 메서드")
    class HasStockTest {

        @Test
        @DisplayName("성공 - 재고 충분")
        void shouldReturnTrueWhenSufficientStock() {
            // given
            stockCounterAdapter.initialize(productStockId, 10);

            // when
            boolean result = stockCounterAdapter.hasStock(productStockId, 5);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("성공 - 재고 부족")
        void shouldReturnFalseWhenInsufficientStock() {
            // given
            stockCounterAdapter.initialize(productStockId, 3);

            // when
            boolean result = stockCounterAdapter.hasStock(productStockId, 5);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("성공 - 키 미존재 시 false 반환")
        void shouldReturnFalseWhenKeyNotExists() {
            // when
            boolean result = stockCounterAdapter.hasStock(productStockId, 1);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getStocks 배치 메서드")
    class GetStocksTest {

        @Test
        @DisplayName("성공 - 복수 상품 재고 조회")
        void shouldReturnMultipleStocks() {
            // given
            stockCounterAdapter.initialize(1L, 10);
            stockCounterAdapter.initialize(2L, 20);
            stockCounterAdapter.initialize(3L, 30);

            // when
            Map<Long, Integer> stocks = stockCounterAdapter.getStocks(List.of(1L, 2L, 3L, 999L));

            // then
            assertThat(stocks)
                    .containsEntry(1L, 10)
                    .containsEntry(2L, 20)
                    .containsEntry(3L, 30)
                    .containsEntry(999L, NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("exists 메서드")
    class ExistsTest {

        @Test
        @DisplayName("성공 - 키 존재")
        void shouldReturnTrueWhenKeyExists() {
            // given
            stockCounterAdapter.initialize(productStockId, 10);

            // when
            boolean exists = stockCounterAdapter.exists(productStockId);

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("성공 - 키 미존재")
        void shouldReturnFalseWhenKeyNotExists() {
            // when
            boolean exists = stockCounterAdapter.exists(productStockId);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("initialize 메서드")
    class InitializeTest {

        @Test
        @DisplayName("성공 - 새 키 초기화")
        void shouldInitializeNewKey() {
            // when
            stockCounterAdapter.initialize(productStockId, 50);

            // then
            assertThat(stockCounterAdapter.getStock(productStockId)).isEqualTo(50);
            assertKeyExists(KEY_PREFIX + productStockId);
        }

        @Test
        @DisplayName("성공 - 기존 키 덮어쓰기")
        void shouldOverwriteExistingKey() {
            // given
            stockCounterAdapter.initialize(productStockId, 10);

            // when
            stockCounterAdapter.initialize(productStockId, 100);

            // then
            assertThat(stockCounterAdapter.getStock(productStockId)).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("initializeAll 배치 메서드")
    class InitializeAllTest {

        @Test
        @DisplayName("성공 - 복수 상품 재고 초기화")
        void shouldInitializeMultipleStocks() {
            // given
            Map<Long, Integer> stocks = Map.of(1L, 10, 2L, 20, 3L, 30);

            // when
            stockCounterAdapter.initializeAll(stocks);

            // then
            assertThat(stockCounterAdapter.getStock(1L)).isEqualTo(10);
            assertThat(stockCounterAdapter.getStock(2L)).isEqualTo(20);
            assertThat(stockCounterAdapter.getStock(3L)).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("성공 - 키 삭제")
        void shouldDeleteKey() {
            // given
            stockCounterAdapter.initialize(productStockId, 10);
            assertKeyExists(KEY_PREFIX + productStockId);

            // when
            stockCounterAdapter.delete(productStockId);

            // then
            assertKeyNotExists(KEY_PREFIX + productStockId);
            assertThat(stockCounterAdapter.getStock(productStockId)).isEqualTo(NOT_FOUND);
        }
    }
}
