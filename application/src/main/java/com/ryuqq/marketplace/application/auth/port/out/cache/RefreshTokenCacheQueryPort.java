package com.ryuqq.marketplace.application.auth.port.out.cache;

import com.ryuqq.marketplace.domain.auth.vo.RefreshTokenCacheKey;
import java.util.Optional;

/**
 * Refresh Token 캐시 Query Port
 *
 * <p>Refresh Token 캐시의 읽기 작업을 위한 아웃바운드 포트입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RefreshTokenCacheQueryPort {

    /**
     * Refresh Token으로 회원 ID를 조회합니다.
     *
     * @param cacheKey 캐시 키
     * @return 회원 ID (Optional)
     */
    Optional<String> findMemberIdByToken(RefreshTokenCacheKey cacheKey);
}
