package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** CanonicalOptionGroup JPA 엔티티. */
@Entity
@Table(name = "canonical_option_group")
public class CanonicalOptionGroupJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    @Column(name = "name_ko", nullable = false, length = 100)
    private String nameKo;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected CanonicalOptionGroupJpaEntity() {
        super();
    }

    private CanonicalOptionGroupJpaEntity(
            Long id,
            String code,
            String nameKo,
            String nameEn,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.active = active;
    }

    public static CanonicalOptionGroupJpaEntity create(
            Long id,
            String code,
            String nameKo,
            String nameEn,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        return new CanonicalOptionGroupJpaEntity(
                id, code, nameKo, nameEn, active, createdAt, updatedAt);
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

    public boolean isActive() {
        return active;
    }
}
