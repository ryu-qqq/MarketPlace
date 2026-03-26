package com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice;

/** 레거시 세토프 호환 고시정보 API 엔드포인트. */
public final class LegacyNoticeEndpoints {

    private LegacyNoticeEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String PRODUCT_GROUP_ID = BASE + "/product/group/{productGroupId}";
    public static final String NOTICE = PRODUCT_GROUP_ID + "/notice";
}
