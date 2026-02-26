package com.ryuqq.marketplace.domain.auth.vo;

/**
 * Refresh Token 캐시 키 VO
 *
 * <p>Refresh Token 캐시에 사용되는 키를 나타냅니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record RefreshTokenCacheKey(String value) {

    private static final String KEY_PREFIX = "cache:refresh-token:";

    /**
     * Refresh Token 캐시 키를 생성합니다.
     *
     * @param refreshToken Refresh Token 값
     * @return RefreshTokenCacheKey
     */
    public static RefreshTokenCacheKey of(String refreshToken) {
        return new RefreshTokenCacheKey(KEY_PREFIX + refreshToken);
    }
}
