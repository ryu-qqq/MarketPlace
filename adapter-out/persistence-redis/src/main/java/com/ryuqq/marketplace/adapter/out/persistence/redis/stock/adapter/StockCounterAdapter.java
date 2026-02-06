package com.ryuqq.marketplace.adapter.out.persistence.redis.stock.adapter;

import com.ryuqq.marketplace.application.common.port.out.StockCounterPort;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * Redis 재고 카운터 Adapter (Lettuce 기반)
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>원자적 재고 차감/증가 (DECRBY/INCRBY)
 *   <li>재고 조회 및 초기화
 *   <li>키 형식: {@code stock:counter:{productStockId}}
 * </ul>
 *
 * <p><strong>원자성 보장:</strong>
 *
 * <p>Redis DECRBY/INCRBY 명령어는 원자적(atomic)이므로 동시성 문제가 발생하지 않습니다. 별도 분산락 없이도 재고 차감/증가의 원자성이 보장됩니다.
 *
 * <p><strong>음수 재고 처리:</strong>
 *
 * <p>DECRBY는 음수를 허용합니다. 음수 결과는 재고 부족을 의미하며, 호출자가 롤백(INCRBY)을 수행해야 합니다.
 *
 * <p><strong>금지 사항:</strong>
 *
 * <ul>
 *   <li>비즈니스 로직 포함 금지
 *   <li>@Transactional 금지
 *   <li>DB 접근 금지
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@Component
public class StockCounterAdapter implements StockCounterPort {

    private static final String KEY_PREFIX = "stock:counter:";
    private static final int NOT_FOUND = -1;

    /**
     * Lua 스크립트: 키 존재 시에만 DECRBY 수행
     *
     * <p>원자적으로 키 존재 여부를 확인하고 재고를 차감합니다. 키가 없으면 -1(NOT_FOUND)을 반환합니다.
     */
    private static final String DECREMENT_IF_EXISTS_SCRIPT =
            """
            local key = KEYS[1]
            local quantity = tonumber(ARGV[1])
            if redis.call('EXISTS', key) == 0 then
                return -1
            end
            return redis.call('DECRBY', key, quantity)
            """;

    /**
     * Lua 스크립트: 키 존재 시에만 INCRBY 수행
     *
     * <p>원자적으로 키 존재 여부를 확인하고 재고를 증가합니다. 키가 없으면 -1(NOT_FOUND)을 반환합니다.
     */
    private static final String INCREMENT_IF_EXISTS_SCRIPT =
            """
            local key = KEYS[1]
            local quantity = tonumber(ARGV[1])
            if redis.call('EXISTS', key) == 0 then
                return -1
            end
            return redis.call('INCRBY', key, quantity)
            """;

    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Long> decrementScript;
    private final DefaultRedisScript<Long> incrementScript;
    private final Duration stockKeyTtl;

    /**
     * 생성자
     *
     * @param redisTemplate Redis 템플릿
     * @param stockKeyTtlSeconds 재고 키 TTL (초 단위, 기본값: 86400초 = 24시간)
     */
    public StockCounterAdapter(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${redis.stock.counter.ttl-seconds:86400}") long stockKeyTtlSeconds) {
        this.redisTemplate = redisTemplate;
        this.decrementScript = new DefaultRedisScript<>(DECREMENT_IF_EXISTS_SCRIPT, Long.class);
        this.incrementScript = new DefaultRedisScript<>(INCREMENT_IF_EXISTS_SCRIPT, Long.class);
        this.stockKeyTtl = Duration.ofSeconds(stockKeyTtlSeconds);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>원자적 DECRBY (키 존재 확인 포함):</strong>
     *
     * <p>Lua 스크립트로 키 존재 여부를 확인한 후 원자적으로 재고를 차감합니다. 키가 없으면 -1(NOT_FOUND)을 반환합니다. 반환값이 음수(NOT_FOUND
     * 제외)인 경우 재고 부족이므로 호출자가 롤백해야 합니다.
     *
     * <p><strong>키 미존재 처리:</strong>
     *
     * <p>키가 존재하지 않으면 -1(NOT_FOUND)을 반환합니다. 이 경우 호출자는 {@link #initialize(Long, int)}로 먼저 키를 초기화해야
     * 합니다.
     */
    @Override
    public int decrement(Long productStockId, int quantity) {
        String key = buildKey(productStockId);
        Long result = redisTemplate.execute(decrementScript, List.of(key), quantity);
        return result != null ? result.intValue() : NOT_FOUND;
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>원자적 INCRBY (키 존재 확인 포함):</strong>
     *
     * <p>Lua 스크립트로 키 존재 여부를 확인한 후 원자적으로 재고를 증가합니다. 키가 없으면 -1(NOT_FOUND)을 반환합니다. 롤백, 주문 취소 시 사용합니다.
     *
     * <p><strong>키 미존재 처리:</strong>
     *
     * <p>키가 존재하지 않으면 -1(NOT_FOUND)을 반환합니다. 이 경우 호출자는 {@link #initialize(Long, int)}로 먼저 키를 초기화해야
     * 합니다.
     */
    @Override
    public int increment(Long productStockId, int quantity) {
        String key = buildKey(productStockId);
        Long result = redisTemplate.execute(incrementScript, List.of(key), quantity);
        return result != null ? result.intValue() : NOT_FOUND;
    }

    /** {@inheritDoc} */
    @Override
    public int getStock(Long productStockId) {
        String key = buildKey(productStockId);
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return NOT_FOUND;
        }

        return parseIntValue(value);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Batch 조회:</strong>
     *
     * <p>MGET 명령어로 여러 키를 한 번에 조회합니다. 네트워크 라운드트립을 최소화합니다.
     */
    @Override
    public Map<Long, Integer> getStocks(List<Long> productStockIds) {
        if (productStockIds == null || productStockIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> keys = productStockIds.stream().map(this::buildKey).toList();

        List<Object> values = redisTemplate.opsForValue().multiGet(keys);

        Map<Long, Integer> result = new HashMap<>();
        for (int i = 0; i < productStockIds.size(); i++) {
            Long productStockId = productStockIds.get(i);
            Object value = (values != null && i < values.size()) ? values.get(i) : null;

            int stock = (value != null) ? parseIntValue(value) : NOT_FOUND;
            result.put(productStockId, stock);
        }

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasStock(Long productStockId, int requiredQuantity) {
        int currentStock = getStock(productStockId);
        return currentStock >= requiredQuantity;
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Batch 재고 확인:</strong>
     *
     * <p>모든 상품의 재고가 충분한지 확인합니다. 하나라도 부족하면 false를 반환합니다.
     */
    @Override
    public boolean hasStocks(Map<Long, Integer> stockRequirements) {
        if (stockRequirements == null || stockRequirements.isEmpty()) {
            return true;
        }

        List<Long> productStockIds = stockRequirements.keySet().stream().toList();
        Map<Long, Integer> currentStocks = getStocks(productStockIds);

        for (Map.Entry<Long, Integer> entry : stockRequirements.entrySet()) {
            Long productStockId = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentStock = currentStocks.getOrDefault(productStockId, NOT_FOUND);

            if (currentStock < requiredQuantity) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>재고 초기화 (SET with TTL):</strong>
     *
     * <p>RDB에서 Redis로 재고를 동기화할 때 사용합니다. 기존 값을 덮어쓰고 TTL을 설정합니다.
     *
     * <p><strong>TTL 정책:</strong>
     *
     * <p>기본 TTL은 24시간입니다. TTL 만료 시 키가 자동 삭제되며, 다음 조회 시 RDB에서 재동기화해야 합니다. {@code
     * redis.stock.counter.ttl-seconds} 설정으로 TTL을 조정할 수 있습니다.
     */
    @Override
    public void initialize(Long productStockId, int quantity) {
        String key = buildKey(productStockId);
        redisTemplate.opsForValue().set(key, quantity, stockKeyTtl);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Batch 초기화 (SETEX with TTL):</strong>
     *
     * <p>여러 상품의 재고를 한 번에 초기화합니다. Pipeline으로 처리하여 성능을 최적화하며, 각 키에 TTL을 설정합니다.
     */
    @Override
    public void initializeAll(Map<Long, Integer> stocks) {
        if (stocks == null || stocks.isEmpty()) {
            return;
        }

        long ttlSeconds = stockKeyTtl.toSeconds();
        redisTemplate.executePipelined(
                (RedisCallback<Object>)
                        connection -> {
                            for (Map.Entry<Long, Integer> entry : stocks.entrySet()) {
                                String key = buildKey(entry.getKey());
                                byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                                byte[] valueBytes =
                                        String.valueOf(entry.getValue())
                                                .getBytes(StandardCharsets.UTF_8);
                                connection.stringCommands().setEx(keyBytes, ttlSeconds, valueBytes);
                            }
                            return null;
                        });
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(Long productStockId) {
        String key = buildKey(productStockId);
        Boolean result = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(result);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long productStockId) {
        String key = buildKey(productStockId);
        redisTemplate.delete(key);
    }

    /**
     * 재고 키 생성
     *
     * @param productStockId 상품 재고 ID
     * @return Redis 키 (stock:counter:{productStockId})
     */
    private String buildKey(Long productStockId) {
        return KEY_PREFIX + productStockId;
    }

    /**
     * Object를 int로 파싱
     *
     * <p>Redis에서 조회한 값이 Number 또는 String일 수 있으므로 안전하게 파싱합니다.
     *
     * @param value Redis에서 조회한 값
     * @return 정수 값
     */
    private int parseIntValue(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String str) {
            return Integer.parseInt(str);
        }
        return NOT_FOUND;
    }
}
