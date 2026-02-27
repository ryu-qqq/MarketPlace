package com.ryuqq.marketplace.adapter.in.rest.commoncode;

/**
 * CommonCode Public API 엔드포인트 상수.
 *
 * <p>인증 없이 접근 가능한 공통 코드 조회 엔드포인트. Gateway에서도 인증/인가를 수행하지 않는 경로.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CommonCodePublicEndpoints {

    private CommonCodePublicEndpoints() {}

    /** 기본 경로 */
    public static final String BASE = "/api/v1/market/public/common-codes";
}
