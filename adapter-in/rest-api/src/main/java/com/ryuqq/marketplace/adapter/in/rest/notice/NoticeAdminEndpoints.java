package com.ryuqq.marketplace.adapter.in.rest.notice;

/** Notice Admin API 엔드포인트 상수. */
public final class NoticeAdminEndpoints {

    private NoticeAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String NOTICE_CATEGORIES = BASE + "/notice-categories";
    public static final String CATEGORY_GROUP = "/category-group/{categoryGroup}";
    public static final String PATH_CATEGORY_GROUP = "categoryGroup";
}
