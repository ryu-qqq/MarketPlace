package com.ryuqq.marketplace.application.legacyauth.port.out;

/**
 * 레거시 리프레시 토큰 캐시 저장 포트.
 *
 * <p>레거시 호환 형태로 Redis에 리프레시 토큰을 저장합니다.
 * 키: "refresh_token:{email}", 값: 리프레시 토큰 문자열.
 */
public interface LegacyTokenCacheCommandPort {

    /**
     * 리프레시 토큰을 캐시에 저장합니다.
     *
     * @param email 셀러 관리자 이메일 (캐시 키)
     * @param refreshToken 리프레시 토큰
     * @param expiresInSeconds TTL (초)
     */
    void persist(String email, String refreshToken, long expiresInSeconds);
}
