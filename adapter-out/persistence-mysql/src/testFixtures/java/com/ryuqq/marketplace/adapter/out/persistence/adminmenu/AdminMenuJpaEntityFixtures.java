package com.ryuqq.marketplace.adapter.out.persistence.adminmenu;

import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.entity.AdminMenuJpaEntity;
import java.time.Instant;

/**
 * AdminMenuJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 AdminMenuJpaEntity 관련 객체들을 생성합니다.
 */
public final class AdminMenuJpaEntityFixtures {

    private AdminMenuJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_TITLE = "판매자 관리";
    public static final String DEFAULT_ICON_NAME = "Users";
    public static final int DEFAULT_DISPLAY_ORDER = 0;
    public static final int DEFAULT_ROLE_LEVEL = 2;

    /** 활성 그룹 Entity (parentId=null, url=null). */
    public static AdminMenuJpaEntity activeGroupEntity() {
        Instant now = Instant.now();
        return AdminMenuJpaEntity.create(
                DEFAULT_ID,
                null,
                DEFAULT_TITLE,
                null,
                DEFAULT_ICON_NAME,
                DEFAULT_DISPLAY_ORDER,
                DEFAULT_ROLE_LEVEL,
                true,
                now,
                now);
    }

    /** 활성 그룹 Entity (ID 지정). */
    public static AdminMenuJpaEntity activeGroupEntity(Long id) {
        Instant now = Instant.now();
        return AdminMenuJpaEntity.create(
                id,
                null,
                DEFAULT_TITLE,
                null,
                DEFAULT_ICON_NAME,
                DEFAULT_DISPLAY_ORDER,
                DEFAULT_ROLE_LEVEL,
                true,
                now,
                now);
    }

    /** 활성 아이템 Entity (parentId, url 존재). */
    public static AdminMenuJpaEntity activeItemEntity(Long id, Long parentId) {
        Instant now = Instant.now();
        return AdminMenuJpaEntity.create(
                id,
                parentId,
                "판매자 입점 관리",
                "/seller/application",
                "UserCheck",
                1,
                DEFAULT_ROLE_LEVEL,
                true,
                now,
                now);
    }

    /** VIEWER 레벨 아이템 Entity. */
    public static AdminMenuJpaEntity viewerItemEntity(Long id, Long parentId) {
        Instant now = Instant.now();
        return AdminMenuJpaEntity.create(
                id, parentId, "통합주문 관리", "/order/list", "ShoppingCart", 0, 0, true, now, now);
    }

    /** 비활성 Entity. */
    public static AdminMenuJpaEntity inactiveEntity() {
        Instant now = Instant.now();
        return AdminMenuJpaEntity.create(100L, null, "비활성 메뉴", null, "X", 99, 0, false, now, now);
    }
}
