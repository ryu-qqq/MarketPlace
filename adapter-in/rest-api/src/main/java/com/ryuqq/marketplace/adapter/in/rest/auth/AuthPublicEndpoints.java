package com.ryuqq.marketplace.adapter.in.rest.auth;

/**
 * Auth Public API 엔드포인트 상수.
 *
 * <p>인증 없이 접근 가능한 공개 인증 엔드포인트.
 * Gateway에서도 인증/인가를 수행하지 않는 경로.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class AuthPublicEndpoints {

    private AuthPublicEndpoints() {}

    /** 공개 인증 API 기본 경로. */
    public static final String BASE = "/api/v1/market/public/auth";

    /** 로그인 경로. */
    public static final String LOGIN = "/login";

    /** 로그아웃 경로. */
    public static final String LOGOUT = "/logout";

    /** 토큰 갱신 경로. */
    public static final String REFRESH = "/refresh";
}
