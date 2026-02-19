package com.ryuqq.marketplace.adapter.in.rest.legacy.category;

/** 레거시 세토프 호환 카테고리 API 엔드포인트. */
public final class LegacyCategoryEndpoints {

    private LegacyCategoryEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String CATEGORY = BASE + "/category";
    public static final String CATEGORY_ID = CATEGORY + "/{categoryId}";
    public static final String CATEGORY_PARENT = CATEGORY + "/parent/{categoryId}";
    public static final String CATEGORY_PARENTS = CATEGORY + "/parents";
    public static final String CATEGORY_PAGE = CATEGORY + "/page";
    public static final String CATEGORY_EXTERNAL_MAPPING = CATEGORY + "/external/{siteId}/mapping";
}
