package com.ryuqq.marketplace.adapter.in.rest.auth;

/**
 * Auth Admin API 엔드포인트 상수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class AuthAdminEndpoints {

    private AuthAdminEndpoints() {}

    /** 인증 API 기본 경로. */
    public static final String BASE = "/api/v1/market/auth";

    /** 로그인 경로. */
    public static final String LOGIN = "/login";

    /** 로그아웃 경로. */
    public static final String LOGOUT = "/logout";

    /** 토큰 갱신 경로. */
    public static final String REFRESH = "/refresh";

    /** 내 정보 조회 경로. */
    public static final String ME = "/me";
}
