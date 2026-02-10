package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** BrandMapping JPA 엔티티. */
@Entity
@Table(name = "brand_mapping")
public class BrandMappingJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sales_channel_brand_id", nullable = false)
    private Long salesChannelBrandId;

    @Column(name = "internal_brand_id", nullable = false)
    private Long internalBrandId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected BrandMappingJpaEntity() {
        super();
    }

    private BrandMappingJpaEntity(
            Long id,
            Long salesChannelBrandId,
            Long internalBrandId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.salesChannelBrandId = salesChannelBrandId;
        this.internalBrandId = internalBrandId;
        this.status = status;
    }

    public static BrandMappingJpaEntity create(
            Long id,
            Long salesChannelBrandId,
            Long internalBrandId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new BrandMappingJpaEntity(
                id, salesChannelBrandId, internalBrandId, status, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSalesChannelBrandId() {
        return salesChannelBrandId;
    }

    public Long getInternalBrandId() {
        return internalBrandId;
    }

    public String getStatus() {
        return status;
    }
}
