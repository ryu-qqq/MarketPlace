package com.ryuqq.marketplace.adapter.in.rest.adminmenu;

/**
 * AdminMenuAdminEndpoints - Admin 메뉴 API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class + private 생성자.
 *
 * <p>API-END-002: static final 상수.
 *
 * <p>API-CTR-012: URL 경로 소문자 + 복수형.
 */
public final class AdminMenuAdminEndpoints {

    private AdminMenuAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** Admin 메뉴 경로 */
    public static final String ADMIN_MENUS = "/api/v1/market/admin/menus";
}
