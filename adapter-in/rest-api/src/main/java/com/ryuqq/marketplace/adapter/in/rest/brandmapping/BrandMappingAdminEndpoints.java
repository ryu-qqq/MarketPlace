package com.ryuqq.marketplace.adapter.in.rest.brandmapping;

/** BrandMapping Admin API 엔드포인트 상수. */
public final class BrandMappingAdminEndpoints {

    private BrandMappingAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String BRAND_MAPPINGS = BASE + "/brand-mappings";
    public static final String BRAND_MAPPING_ID = "/{brandMappingId}";
    public static final String PATH_BRAND_MAPPING_ID = "brandMappingId";
}
