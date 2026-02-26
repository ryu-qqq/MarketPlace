package com.ryuqq.marketplace.adapter.out.persistence.adminmenu.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * AdminMenuJpaEntity - Admin 메뉴 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지.
 *
 * <p>PER-ENT-003: ID 필드는 @GeneratedValue(strategy = IDENTITY).
 *
 * <p>PER-ENT-004: Lombok 사용 금지 - 수동 Getter/생성자.
 */
@Entity
@Table(name = "admin_menus")
public class AdminMenuJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "url", length = 200)
    private String url;

    @Column(name = "icon_name", length = 50)
    private String iconName;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "required_role_level", nullable = false)
    private int requiredRoleLevel;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected AdminMenuJpaEntity() {
        super();
    }

    private AdminMenuJpaEntity(
            Long id,
            Long parentId,
            String title,
            String url,
            String iconName,
            int displayOrder,
            int requiredRoleLevel,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.url = url;
        this.iconName = iconName;
        this.displayOrder = displayOrder;
        this.requiredRoleLevel = requiredRoleLevel;
        this.active = active;
    }

    public static AdminMenuJpaEntity create(
            Long id,
            Long parentId,
            String title,
            String url,
            String iconName,
            int displayOrder,
            int requiredRoleLevel,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        return new AdminMenuJpaEntity(
                id,
                parentId,
                title,
                url,
                iconName,
                displayOrder,
                requiredRoleLevel,
                active,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getIconName() {
        return iconName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public int getRequiredRoleLevel() {
        return requiredRoleLevel;
    }

    public boolean isActive() {
        return active;
    }
}
