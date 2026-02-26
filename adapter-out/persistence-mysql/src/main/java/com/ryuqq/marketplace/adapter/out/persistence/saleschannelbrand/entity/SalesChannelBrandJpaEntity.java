package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** SalesChannelBrand JPA 엔티티. */
@Entity
@Table(name = "sales_channel_brand")
public class SalesChannelBrandJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sales_channel_id", nullable = false)
    private Long salesChannelId;

    @Column(name = "external_brand_code", nullable = false, length = 200)
    private String externalBrandCode;

    @Column(name = "external_brand_name", nullable = false, length = 500)
    private String externalBrandName;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected SalesChannelBrandJpaEntity() {
        super();
    }

    private SalesChannelBrandJpaEntity(
            Long id,
            Long salesChannelId,
            String externalBrandCode,
            String externalBrandName,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.externalBrandCode = externalBrandCode;
        this.externalBrandName = externalBrandName;
        this.status = status;
    }

    public static SalesChannelBrandJpaEntity create(
            Long id,
            Long salesChannelId,
            String externalBrandCode,
            String externalBrandName,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new SalesChannelBrandJpaEntity(
                id,
                salesChannelId,
                externalBrandCode,
                externalBrandName,
                status,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSalesChannelId() {
        return salesChannelId;
    }

    public String getExternalBrandCode() {
        return externalBrandCode;
    }

    public String getExternalBrandName() {
        return externalBrandName;
    }

    public String getStatus() {
        return status;
    }
}
