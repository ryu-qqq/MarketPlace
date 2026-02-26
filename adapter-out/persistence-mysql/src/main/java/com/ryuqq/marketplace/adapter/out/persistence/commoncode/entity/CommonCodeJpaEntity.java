package com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * CommonCodeJpaEntity - 공통 코드 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 *
 * <p>PER-ENT-003: ID 필드는 @GeneratedValue(strategy = IDENTITY).
 *
 * <p>PER-ENT-004: Lombok 사용 금지 - 수동 Getter/생성자.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "common_codes")
public class CommonCodeJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "common_code_type_id", nullable = false)
    private Long commonCodeTypeId;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    protected CommonCodeJpaEntity() {
        super();
    }

    private CommonCodeJpaEntity(
            Long id,
            Long commonCodeTypeId,
            String code,
            String displayName,
            int displayOrder,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.commonCodeTypeId = commonCodeTypeId;
        this.code = code;
        this.displayName = displayName;
        this.displayOrder = displayOrder;
        this.active = active;
    }

    public static CommonCodeJpaEntity create(
            Long id,
            Long commonCodeTypeId,
            String code,
            String displayName,
            int displayOrder,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new CommonCodeJpaEntity(
                id,
                commonCodeTypeId,
                code,
                displayName,
                displayOrder,
                active,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getCommonCodeTypeId() {
        return commonCodeTypeId;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public boolean isActive() {
        return active;
    }
}
