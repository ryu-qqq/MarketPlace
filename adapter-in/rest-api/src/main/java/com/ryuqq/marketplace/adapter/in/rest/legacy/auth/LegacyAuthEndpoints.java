package com.ryuqq.marketplace.adapter.in.rest.legacy.auth;

/** 레거시 세토프 호환 인증 API 엔드포인트. */
public final class LegacyAuthEndpoints {

    private LegacyAuthEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String AUTH = BASE + "/auth";
    public static final String AUTH_AUTHENTICATION = AUTH + "/authentication";
}
