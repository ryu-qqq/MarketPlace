package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacySellerIdMappingJpaEntity - 레거시 셀러 ID 매핑 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지.
 */
@Entity
@Table(name = "legacy_seller_id_mapping")
public class LegacySellerIdMappingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_seller_id", nullable = false, unique = true)
    private Long legacySellerId;

    @Column(name = "internal_seller_id", nullable = false)
    private Long internalSellerId;

    @Column(name = "seller_name", nullable = false, length = 100)
    private String sellerName;

    protected LegacySellerIdMappingJpaEntity() {}

    public Long getId() {
        return id;
    }

    public Long getLegacySellerId() {
        return legacySellerId;
    }

    public Long getInternalSellerId() {
        return internalSellerId;
    }

    public String getSellerName() {
        return sellerName;
    }
}
