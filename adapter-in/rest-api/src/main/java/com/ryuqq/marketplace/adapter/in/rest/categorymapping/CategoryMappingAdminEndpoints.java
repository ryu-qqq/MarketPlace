package com.ryuqq.marketplace.adapter.in.rest.categorymapping;

/** CategoryMapping Admin API 엔드포인트 상수. */
public final class CategoryMappingAdminEndpoints {

    private CategoryMappingAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String CATEGORY_MAPPINGS = BASE + "/category-mappings";
    public static final String CATEGORY_MAPPING_ID = "/{categoryMappingId}";
    public static final String PATH_CATEGORY_MAPPING_ID = "categoryMappingId";
}
