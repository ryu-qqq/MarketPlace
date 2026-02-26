package com.ryuqq.marketplace.adapter.out.persistence.notice.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** NoticeCategory JPA 엔티티. */
@Entity
@Table(name = "notice_category")
public class NoticeCategoryJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    @Column(name = "name_ko", nullable = false, length = 100)
    private String nameKo;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "target_category_group", nullable = false, length = 50, unique = true)
    private String targetCategoryGroup;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected NoticeCategoryJpaEntity() {
        super();
    }

    private NoticeCategoryJpaEntity(
            Long id,
            String code,
            String nameKo,
            String nameEn,
            String targetCategoryGroup,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.targetCategoryGroup = targetCategoryGroup;
        this.active = active;
    }

    public static NoticeCategoryJpaEntity create(
            Long id,
            String code,
            String nameKo,
            String nameEn,
            String targetCategoryGroup,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        return new NoticeCategoryJpaEntity(
                id, code, nameKo, nameEn, targetCategoryGroup, active, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getNameKo() {
        return nameKo;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getTargetCategoryGroup() {
        return targetCategoryGroup;
    }

    public boolean isActive() {
        return active;
    }
}
