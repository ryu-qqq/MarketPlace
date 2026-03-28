package com.ryuqq.marketplace.adapter.in.rest.legacy.session;

/** 레거시 세토프 호환 이미지 업로드 세션 API 엔드포인트. */
public final class LegacySessionEndpoints {

    private LegacySessionEndpoints() {}

    private static final String BASE = "/api/v1";

    public static final String IMAGE = BASE + "/image";
    public static final String IMAGE_PRESIGNED = IMAGE + "/presigned";
}
