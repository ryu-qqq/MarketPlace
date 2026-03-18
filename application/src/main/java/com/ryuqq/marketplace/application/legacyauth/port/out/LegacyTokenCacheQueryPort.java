package com.ryuqq.marketplace.application.legacyauth.port.out;

import java.util.Optional;

/**
 * 레거시 리프레시 토큰 캐시 조회 포트.
 *
 * <p>Redis에서 리프레시 토큰을 조회합니다.
 */
public interface LegacyTokenCacheQueryPort {

    /**
     * 이메일로 리프레시 토큰 조회.
     *
     * @param email 셀러 관리자 이메일
     * @return 리프레시 토큰 Optional
     */
    Optional<String> findByEmail(String email);
}
