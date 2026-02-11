package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** CanonicalOptionValue JPA 엔티티. */
@Entity
@Table(name = "canonical_option_value")
public class CanonicalOptionValueJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "canonical_option_group_id", nullable = false)
    private Long canonicalOptionGroupId;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name_ko", nullable = false, length = 100)
    private String nameKo;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected CanonicalOptionValueJpaEntity() {
        super();
    }

    private CanonicalOptionValueJpaEntity(
            Long id,
            Long canonicalOptionGroupId,
            String code,
            String nameKo,
            String nameEn,
            int sortOrder,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.canonicalOptionGroupId = canonicalOptionGroupId;
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.sortOrder = sortOrder;
    }

    public static CanonicalOptionValueJpaEntity create(
            Long id,
            Long canonicalOptionGroupId,
            String code,
            String nameKo,
            String nameEn,
            int sortOrder,
            Instant createdAt,
            Instant updatedAt) {
        return new CanonicalOptionValueJpaEntity(
                id, canonicalOptionGroupId, code, nameKo, nameEn, sortOrder,
                createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getCanonicalOptionGroupId() {
        return canonicalOptionGroupId;
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

    public int getSortOrder() {
        return sortOrder;
    }
}
