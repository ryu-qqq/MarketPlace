package com.ryuqq.marketplace.domain.adminmenu.id;

/** Admin 메뉴 ID Value Object. */
public record AdminMenuId(Long value) {

    public static AdminMenuId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("AdminMenuId 값은 null일 수 없습니다");
        }
        return new AdminMenuId(value);
    }

    public static AdminMenuId forNew() {
        return new AdminMenuId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
