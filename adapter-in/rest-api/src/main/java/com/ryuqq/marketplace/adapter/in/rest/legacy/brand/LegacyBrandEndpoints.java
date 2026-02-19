package com.ryuqq.marketplace.adapter.in.rest.legacy.brand;

/** 레거시 세토프 호환 브랜드 API 엔드포인트. */
public final class LegacyBrandEndpoints {

    private LegacyBrandEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String BRANDS = BASE + "/brands";
    public static final String BRAND_EXTERNAL_MAPPING = BASE + "/brand/external/{siteId}/mapping";
}
