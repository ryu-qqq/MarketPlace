package com.ryuqq.marketplace.domain.adminmenu;

import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import com.ryuqq.marketplace.domain.adminmenu.id.AdminMenuId;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import java.time.Instant;

/**
 * AdminMenu 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 AdminMenu 관련 객체들을 생성합니다.
 */
public final class AdminMenuFixtures {

    private AdminMenuFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PARENT_ID = null;
    public static final String DEFAULT_TITLE = "판매자 관리";
    public static final String DEFAULT_URL = null;
    public static final String DEFAULT_ICON_NAME = "Users";
    public static final int DEFAULT_DISPLAY_ORDER = 0;
    public static final AdminRole DEFAULT_REQUIRED_ROLE = AdminRole.ADMIN;

    // ===== AdminMenu Aggregate Fixtures =====

    /** 활성 그룹 메뉴 (parentId=null, url=null) 생성. */
    public static AdminMenu activeGroupMenu() {
        Instant now = Instant.now();
        return AdminMenu.reconstitute(
                AdminMenuId.of(DEFAULT_ID),
                DEFAULT_PARENT_ID,
                DEFAULT_TITLE,
                DEFAULT_URL,
                DEFAULT_ICON_NAME,
                DEFAULT_DISPLAY_ORDER,
                DEFAULT_REQUIRED_ROLE,
                true,
                now,
                now);
    }

    /** 활성 그룹 메뉴 (ID 지정) 생성. */
    public static AdminMenu activeGroupMenu(Long id) {
        Instant now = Instant.now();
        return AdminMenu.reconstitute(
                AdminMenuId.of(id),
                DEFAULT_PARENT_ID,
                DEFAULT_TITLE,
                DEFAULT_URL,
                DEFAULT_ICON_NAME,
                DEFAULT_DISPLAY_ORDER,
                DEFAULT_REQUIRED_ROLE,
                true,
                now,
                now);
    }

    /** 활성 아이템 메뉴 (parentId 존재, url 존재) 생성. */
    public static AdminMenu activeItemMenu(Long id, Long parentId) {
        Instant now = Instant.now();
        return AdminMenu.reconstitute(
                AdminMenuId.of(id),
                parentId,
                "판매자 입점 관리",
                "/seller/application",
                "UserCheck",
                1,
                AdminRole.ADMIN,
                true,
                now,
                now);
    }

    /** 새 그룹 메뉴 (forNew) 생성. */
    public static AdminMenu newGroupMenu() {
        Instant now = Instant.now();
        return AdminMenu.forNew(
                null,
                DEFAULT_TITLE,
                null,
                DEFAULT_ICON_NAME,
                DEFAULT_DISPLAY_ORDER,
                DEFAULT_REQUIRED_ROLE,
                now);
    }

    /** 새 아이템 메뉴 (forNew) 생성. */
    public static AdminMenu newItemMenu(Long parentId) {
        Instant now = Instant.now();
        return AdminMenu.forNew(
                parentId, "판매자 입점 관리", "/seller/application", "UserCheck", 1, AdminRole.ADMIN, now);
    }

    /** 비활성 메뉴 생성. */
    public static AdminMenu inactiveMenu() {
        Instant now = Instant.now();
        return AdminMenu.reconstitute(
                AdminMenuId.of(100L),
                null,
                "비활성 메뉴",
                null,
                "X",
                99,
                AdminRole.VIEWER,
                false,
                now,
                now);
    }

    /** VIEWER 레벨 아이템 메뉴 생성. */
    public static AdminMenu viewerItemMenu(Long id, Long parentId) {
        Instant now = Instant.now();
        return AdminMenu.reconstitute(
                AdminMenuId.of(id),
                parentId,
                "통합주문 관리",
                "/order/list",
                "ShoppingCart",
                0,
                AdminRole.VIEWER,
                true,
                now,
                now);
    }

    /** SUPER_ADMIN 레벨 아이템 메뉴 생성. */
    public static AdminMenu superAdminItemMenu(Long id, Long parentId) {
        Instant now = Instant.now();
        return AdminMenu.reconstitute(
                AdminMenuId.of(id),
                parentId,
                "정산 관리",
                "/settlement",
                "DollarSign",
                0,
                AdminRole.SUPER_ADMIN,
                true,
                now,
                now);
    }
}
