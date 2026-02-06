package com.ryuqq.marketplace.application.auth.port.out.cache;

import com.ryuqq.marketplace.domain.auth.vo.RefreshTokenCacheKey;

/**
 * Refresh Token 캐시 Command Port
 *
 * <p>Refresh Token 캐시의 쓰기/삭제 작업을 위한 아웃바운드 포트입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RefreshTokenCacheCommandPort {

    /**
     * Refresh Token을 캐시에 저장합니다.
     *
     * @param cacheKey 캐시 키
     * @param memberId 회원 ID
     * @param expiresInSeconds 만료 시간 (초)
     */
    void persist(RefreshTokenCacheKey cacheKey, String memberId, long expiresInSeconds);

    /**
     * Refresh Token 캐시를 삭제합니다.
     *
     * @param cacheKey 캐시 키
     */
    void delete(RefreshTokenCacheKey cacheKey);
}
