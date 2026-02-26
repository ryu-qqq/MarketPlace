package com.ryuqq.marketplace.adapter.out.persistence.redis.cache.adapter;

import com.ryuqq.marketplace.application.auth.port.out.cache.RefreshTokenCacheCommandPort;
import com.ryuqq.marketplace.application.auth.port.out.cache.RefreshTokenCacheQueryPort;
import com.ryuqq.marketplace.domain.auth.vo.RefreshTokenCacheKey;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Refresh Token 캐시 Adapter (Lettuce 기반)
 *
 * <p>Command Port와 Query Port를 모두 구현하여 Redis 캐시 작업을 처리합니다.
 *
 * <p><strong>캐시 구조 (단방향):</strong>
 *
 * <ul>
 *   <li>Key: cache:refresh-token:{token}
 *   <li>Value: memberId (UUID 문자열)
 * </ul>
 *
 * <p><strong>금지 사항:</strong>
 *
 * <ul>
 *   <li>비즈니스 로직 포함 금지
 *   <li>@Transactional 금지
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@Component
public class RefreshTokenCacheAdapter
        implements RefreshTokenCacheCommandPort, RefreshTokenCacheQueryPort {

    private final RedisTemplate<String, Object> redisTemplate;

    public RefreshTokenCacheAdapter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Refresh Token을 캐시에 저장합니다.
     */
    @Override
    public void persist(RefreshTokenCacheKey cacheKey, String memberId, long expiresInSeconds) {
        redisTemplate
                .opsForValue()
                .set(cacheKey.value(), memberId, Duration.ofSeconds(expiresInSeconds));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Refresh Token으로 회원 ID를 조회합니다.
     */
    @Override
    public Optional<String> findMemberIdByToken(RefreshTokenCacheKey cacheKey) {
        Object value = redisTemplate.opsForValue().get(cacheKey.value());

        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(value.toString());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Refresh Token 캐시를 삭제합니다.
     */
    @Override
    public void delete(RefreshTokenCacheKey cacheKey) {
        redisTemplate.delete(cacheKey.value());
    }
}
