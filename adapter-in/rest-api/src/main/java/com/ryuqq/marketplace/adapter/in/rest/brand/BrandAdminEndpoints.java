package com.ryuqq.marketplace.adapter.in.rest.brand;

/** Brand Admin API 엔드포인트 상수. */
public final class BrandAdminEndpoints {

    private BrandAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String BRANDS = BASE + "/brands";
    public static final String BRAND_ID = "/{brandId}";
}
