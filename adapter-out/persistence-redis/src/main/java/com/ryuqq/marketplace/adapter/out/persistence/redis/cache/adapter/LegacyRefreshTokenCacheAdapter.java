package com.ryuqq.marketplace.adapter.out.persistence.redis.cache.adapter;

import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenCacheCommandPort;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenCacheQueryPort;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 레거시 리프레시 토큰 캐시 Adapter.
 *
 * <p>레거시 호환 키 포맷: "refresh_token:{email}"
 */
@Component
public class LegacyRefreshTokenCacheAdapter
        implements LegacyTokenCacheCommandPort, LegacyTokenCacheQueryPort {

    private static final String KEY_PREFIX = "refresh_token:";

    private final RedisTemplate<String, Object> redisTemplate;

    public LegacyRefreshTokenCacheAdapter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void persist(String email, String refreshToken, long expiresInSeconds) {
        redisTemplate
                .opsForValue()
                .set(KEY_PREFIX + email, refreshToken, Duration.ofSeconds(expiresInSeconds));
    }

    @Override
    public Optional<String> findByEmail(String email) {
        Object value = redisTemplate.opsForValue().get(KEY_PREFIX + email);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value.toString());
    }
}
