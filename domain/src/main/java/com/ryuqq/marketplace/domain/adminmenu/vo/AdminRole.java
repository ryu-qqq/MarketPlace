package com.ryuqq.marketplace.domain.adminmenu.vo;

/** Admin 역할 Enum VO. */
public enum AdminRole {
    VIEWER(0),
    EDITOR(1),
    ADMIN(2),
    SUPER_ADMIN(3);

    private final int level;

    AdminRole(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }

    /**
     * 이 역할이 요구 역할에 접근 가능한지 확인.
     *
     * @param required 요구 역할
     * @return 접근 가능하면 true
     */
    public boolean canAccess(AdminRole required) {
        return this.level >= required.level;
    }

    /**
     * 역할명 문자열 → enum 변환.
     *
     * @param name 역할명 (e.g. "ADMIN")
     * @return AdminRole enum
     * @throws IllegalArgumentException 매칭되지 않는 경우
     */
    public static AdminRole fromName(String name) {
        for (AdminRole role : values()) {
            if (role.name().equals(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("알 수 없는 AdminRole: " + name);
    }

    /**
     * 역할 레벨 → enum 변환.
     *
     * @param level 역할 레벨
     * @return AdminRole enum
     * @throws IllegalArgumentException 매칭되지 않는 경우
     */
    public static AdminRole fromLevel(int level) {
        for (AdminRole role : values()) {
            if (role.level == level) {
                return role;
            }
        }
        throw new IllegalArgumentException("알 수 없는 AdminRole level: " + level);
    }
}
