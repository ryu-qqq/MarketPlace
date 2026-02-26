package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "outbound_products")
public class OutboundProductJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Column(name = "sales_channel_id", nullable = false)
    private Long salesChannelId;

    @Column(name = "external_product_id", length = 255)
    private String externalProductId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    protected OutboundProductJpaEntity() {
        super();
    }

    private OutboundProductJpaEntity(
            Long id,
            Long productGroupId,
            Long salesChannelId,
            String externalProductId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.productGroupId = productGroupId;
        this.salesChannelId = salesChannelId;
        this.externalProductId = externalProductId;
        this.status = status;
    }

    public static OutboundProductJpaEntity create(
            Long id,
            Long productGroupId,
            Long salesChannelId,
            String externalProductId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new OutboundProductJpaEntity(
                id,
                productGroupId,
                salesChannelId,
                externalProductId,
                status,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public Long getSalesChannelId() {
        return salesChannelId;
    }

    public String getExternalProductId() {
        return externalProductId;
    }

    public String getStatus() {
        return status;
    }
}
