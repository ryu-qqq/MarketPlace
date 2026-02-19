package com.ryuqq.marketplace.adapter.in.rest.legacy.auth;

/** 레거시 세토프 호환 인증 API 엔드포인트. */
public final class LegacyAuthEndpoints {

    private LegacyAuthEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String AUTH = BASE + "/auth";
    public static final String AUTH_AUTHENTICATION = AUTH + "/authentication";
    public static final String AUTH_ID = AUTH + "/{authId}";
    public static final String AUTH_ADMIN_VALIDATION = AUTH + "/admin-validation";
    public static final String AUTH_SELLER = AUTH + "/{sellerId}";
    public static final String AUTH_APPROVAL_STATUS = AUTH + "/approval-status";
}
