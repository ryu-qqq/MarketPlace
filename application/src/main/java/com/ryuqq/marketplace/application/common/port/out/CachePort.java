package com.ryuqq.marketplace.application.common.port.out;

import java.time.Duration;
import java.util.Optional;

/**
 * Cache Port
 *
 * <p>캐시 작업을 위한 아웃바운드 포트입니다.
 *
 * <p><strong>구현체:</strong>
 *
 * <ul>
 *   <li>ObjectCacheAdapter - 객체 타입 Redis 캐시
 *   <li>StringCacheAdapter - 문자열 타입 Redis 캐시
 * </ul>
 *
 * @param <T> 캐시 값 타입
 * @author development-team
 * @since 1.0.0
 */
public interface CachePort<T> {

    /**
     * 기본 TTL로 캐시를 저장합니다.
     *
     * @param key 캐시 키
     * @param value 캐시 값
     */
    void set(String key, T value);

    /**
     * 지정된 TTL로 캐시를 저장합니다.
     *
     * @param key 캐시 키
     * @param value 캐시 값
     * @param ttl 만료 시간
     */
    void set(String key, T value, Duration ttl);

    /**
     * 캐시를 조회합니다.
     *
     * @param key 캐시 키
     * @return 캐시 값 (Optional)
     */
    Optional<T> get(String key);

    /**
     * 캐시를 지정된 타입으로 조회합니다.
     *
     * @param key 캐시 키
     * @param clazz 대상 타입
     * @return 캐시 값 (Optional)
     */
    Optional<T> get(String key, Class<T> clazz);

    /**
     * 캐시를 삭제합니다.
     *
     * @param key 캐시 키
     */
    void evict(String key);

    /**
     * 패턴에 매칭되는 모든 캐시를 삭제합니다.
     *
     * @param pattern 키 패턴 (glob 스타일)
     */
    void evictByPattern(String pattern);

    /**
     * 캐시 존재 여부를 확인합니다.
     *
     * @param key 캐시 키
     * @return 존재 여부
     */
    boolean exists(String key);

    /**
     * 캐시의 남은 TTL을 조회합니다.
     *
     * @param key 캐시 키
     * @return 남은 TTL (키가 없거나 TTL이 없으면 null)
     */
    Duration getTtl(String key);
}
