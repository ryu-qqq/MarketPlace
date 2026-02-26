package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * LegacyProductIdMappingJpaEntity - 레거시 상품(SKU) ID 매핑 JPA 엔티티.
 *
 * <p>레거시 Product ID와 내부 Product ID 간의 매핑을 저장합니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "legacy_product_id_mappings")
public class LegacyProductIdMappingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_product_id", nullable = false, unique = true)
    private Long legacyProductId;

    @Column(name = "internal_product_id", nullable = false)
    private Long internalProductId;

    @Column(name = "legacy_product_group_id", nullable = false)
    private Long legacyProductGroupId;

    @Column(name = "internal_product_group_id", nullable = false)
    private Long internalProductGroupId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LegacyProductIdMappingJpaEntity() {}

    private LegacyProductIdMappingJpaEntity(
            Long id,
            Long legacyProductId,
            Long internalProductId,
            Long legacyProductGroupId,
            Long internalProductGroupId,
            Instant createdAt) {
        this.id = id;
        this.legacyProductId = legacyProductId;
        this.internalProductId = internalProductId;
        this.legacyProductGroupId = legacyProductGroupId;
        this.internalProductGroupId = internalProductGroupId;
        this.createdAt = createdAt;
    }

    public static LegacyProductIdMappingJpaEntity create(
            Long id,
            Long legacyProductId,
            Long internalProductId,
            Long legacyProductGroupId,
            Long internalProductGroupId,
            Instant createdAt) {
        return new LegacyProductIdMappingJpaEntity(
                id,
                legacyProductId,
                internalProductId,
                legacyProductGroupId,
                internalProductGroupId,
                createdAt);
    }

    public Long getId() {
        return id;
    }

    public Long getLegacyProductId() {
        return legacyProductId;
    }

    public Long getInternalProductId() {
        return internalProductId;
    }

    public Long getLegacyProductGroupId() {
        return legacyProductGroupId;
    }

    public Long getInternalProductGroupId() {
        return internalProductGroupId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
