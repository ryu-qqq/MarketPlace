package com.ryuqq.marketplace.adapter.out.persistence.redis.shadow.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.application.shadow.dto.ShadowSnapshot;
import com.ryuqq.marketplace.application.shadow.port.out.ShadowSnapshotStore;
import java.time.Duration;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Shadow 스냅샷 Redis 저장소 구현체.
 *
 * <p>키 형식: shadow:snapshot:{correlationId}
 *
 * <p>TTL: 10분 (DMS 복제 + Lambda 처리 시간 고려)
 *
 * <p>Python Shadow Lambda가 이 키로 조회하여 GET API 응답과 비교합니다.
 */
@Component
public class ShadowSnapshotRedisAdapter implements ShadowSnapshotStore {

    private static final Logger log = LoggerFactory.getLogger(ShadowSnapshotRedisAdapter.class);

    private static final String KEY_PREFIX = "shadow:snapshot:";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public ShadowSnapshotRedisAdapter(
            RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(ShadowSnapshot snapshot) {
        String key = KEY_PREFIX + snapshot.correlationId();
        try {
            String json = objectMapper.writeValueAsString(snapshot);
            redisTemplate.opsForValue().set(key, json, TTL);
        } catch (JsonProcessingException e) {
            log.error("[Shadow] 스냅샷 직렬화 실패. correlationId={}", snapshot.correlationId(), e);
        }
    }

    @Override
    public Optional<ShadowSnapshot> findByCorrelationId(String correlationId) {
        String key = KEY_PREFIX + correlationId;
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return Optional.empty();
        }

        try {
            String json = value instanceof String s ? s : objectMapper.writeValueAsString(value);
            return Optional.of(objectMapper.readValue(json, ShadowSnapshot.class));
        } catch (JsonProcessingException e) {
            log.error("[Shadow] 스냅샷 역직렬화 실패. correlationId={}", correlationId, e);
            return Optional.empty();
        }
    }
}
