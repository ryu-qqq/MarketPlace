package com.ryuqq.marketplace.adapter.out.client.legacyauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 레거시 JWT 설정 프로퍼티.
 *
 * <p>setof-commerce와 동일한 secret/만료시간 사용.
 */
@ConfigurationProperties(prefix = "legacy.token")
public class LegacyJwtProperties {

    private String secret;
    private long accessTokenExpireTime = 1800000L;
    private long refreshTokenExpireTime = 10800000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpireTime() {
        return accessTokenExpireTime;
    }

    public void setAccessTokenExpireTime(long accessTokenExpireTime) {
        this.accessTokenExpireTime = accessTokenExpireTime;
    }

    public long getRefreshTokenExpireTime() {
        return refreshTokenExpireTime;
    }

    public void setRefreshTokenExpireTime(long refreshTokenExpireTime) {
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }
}
