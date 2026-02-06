package com.ryuqq.marketplace.adapter.in.rest.commoncode;

/**
 * CommonCodeAdminEndpoints - 공통 코드 Admin 엔드포인트 정의.
 *
 * <p>API-END-001: Endpoints 클래스에서 모든 엔드포인트 경로 정의.
 *
 * <p>API-END-002: BASE 경로와 각 엔드포인트 경로를 상수로 관리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class CommonCodeAdminEndpoints {

    private CommonCodeAdminEndpoints() {}

    /** 기본 경로 */
    public static final String BASE = "/api/v1/market/common-codes";

    // ========== Query Endpoints ==========

    /** 공통 코드 목록 조회 */
    public static final String SEARCH = "";

    // ========== Command Endpoints ==========

    /** 공통 코드 등록 */
    public static final String REGISTER = "";

    /** 공통 코드 수정 */
    public static final String UPDATE = "/{id}";

    /** 공통 코드 활성화 상태 변경 */
    public static final String CHANGE_ACTIVE_STATUS = "/active-status";
}
