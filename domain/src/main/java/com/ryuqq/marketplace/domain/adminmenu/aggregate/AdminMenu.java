package com.ryuqq.marketplace.domain.adminmenu.aggregate;

import com.ryuqq.marketplace.domain.adminmenu.id.AdminMenuId;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import java.time.Instant;

/** Admin 메뉴 Aggregate Root. */
public class AdminMenu {

    private final AdminMenuId id;
    private final Long parentId;
    private final String title;
    private final String url;
    private final String iconName;
    private final int displayOrder;
    private final AdminRole requiredRole;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;

    private AdminMenu(
            AdminMenuId id,
            Long parentId,
            String title,
            String url,
            String iconName,
            int displayOrder,
            AdminRole requiredRole,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.url = url;
        this.iconName = iconName;
        this.displayOrder = displayOrder;
        this.requiredRole = requiredRole;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AdminMenu forNew(
            Long parentId,
            String title,
            String url,
            String iconName,
            int displayOrder,
            AdminRole requiredRole,
            Instant now) {
        return new AdminMenu(
                AdminMenuId.forNew(),
                parentId,
                title,
                url,
                iconName,
                displayOrder,
                requiredRole,
                true,
                now,
                now);
    }

    public static AdminMenu reconstitute(
            AdminMenuId id,
            Long parentId,
            String title,
            String url,
            String iconName,
            int displayOrder,
            AdminRole requiredRole,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        return new AdminMenu(
                id,
                parentId,
                title,
                url,
                iconName,
                displayOrder,
                requiredRole,
                active,
                createdAt,
                updatedAt);
    }

    /**
     * 사용자 역할이 이 메뉴에 접근 가능한지 확인.
     *
     * @param userRole 사용자 역할
     * @return 접근 가능하면 true
     */
    public boolean isAccessibleBy(AdminRole userRole) {
        return userRole.canAccess(this.requiredRole);
    }

    /**
     * 그룹 메뉴 여부 확인 (url이 null이면 그룹).
     *
     * @return 그룹이면 true
     */
    public boolean isGroup() {
        return url == null;
    }

    public boolean isNew() {
        return id.isNew();
    }

    // Getters
    public AdminMenuId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long parentId() {
        return parentId;
    }

    public String title() {
        return title;
    }

    public String url() {
        return url;
    }

    public String iconName() {
        return iconName;
    }

    public int displayOrder() {
        return displayOrder;
    }

    public AdminRole requiredRole() {
        return requiredRole;
    }

    public boolean isActive() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
