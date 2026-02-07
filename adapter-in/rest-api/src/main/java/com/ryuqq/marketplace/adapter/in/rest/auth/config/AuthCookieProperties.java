package com.ryuqq.marketplace.adapter.in.rest.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 인증 쿠키 설정 프로퍼티.
 *
 * <p>환경별 설정 파일(rest-api-{profile}.yml)에서 값을 주입받습니다.
 *
 * <p>게이트웨이의 JwtAuthenticationFilter, TokenRefreshFilter와 연동되는 쿠키 이름(access_token, refresh_token)은
 * 고정값입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "api.auth.cookie")
public record AuthCookieProperties(
        long accessMaxAge,
        long refreshMaxAge,
        boolean secure,
        String sameSite,
        String path,
        boolean httpOnly) {

    /** 게이트웨이 JwtAuthenticationFilter에서 읽는 쿠키 이름. */
    public static final String ACCESS_TOKEN_COOKIE = "access_token";

    /** 게이트웨이 TokenRefreshFilter에서 읽는 쿠키 이름. */
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";
}
