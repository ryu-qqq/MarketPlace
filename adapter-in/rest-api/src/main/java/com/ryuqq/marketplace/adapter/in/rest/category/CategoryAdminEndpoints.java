package com.ryuqq.marketplace.adapter.in.rest.category;

/** Category Admin API 엔드포인트 상수. */
public final class CategoryAdminEndpoints {

    private CategoryAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String CATEGORIES = BASE + "/categories";
    public static final String CATEGORY_ID = "/{categoryId}";
}
