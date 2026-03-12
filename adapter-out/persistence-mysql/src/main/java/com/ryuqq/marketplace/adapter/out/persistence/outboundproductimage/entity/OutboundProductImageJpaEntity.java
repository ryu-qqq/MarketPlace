package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "outbound_product_images")
public class OutboundProductImageJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outbound_product_id", nullable = false)
    private Long outboundProductId;

    @Column(name = "product_group_image_id")
    private Long productGroupImageId;

    @Column(name = "origin_url", nullable = false, length = 500)
    private String originUrl;

    @Column(name = "external_url", length = 500)
    private String externalUrl;

    @Column(name = "image_type", nullable = false, length = 50)
    private String imageType;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected OutboundProductImageJpaEntity() {
        super();
    }

    private OutboundProductImageJpaEntity(
            Long id,
            Long outboundProductId,
            Long productGroupImageId,
            String originUrl,
            String externalUrl,
            String imageType,
            int sortOrder,
            boolean deleted,
            Instant deletedAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.outboundProductId = outboundProductId;
        this.productGroupImageId = productGroupImageId;
        this.originUrl = originUrl;
        this.externalUrl = externalUrl;
        this.imageType = imageType;
        this.sortOrder = sortOrder;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public static OutboundProductImageJpaEntity create(
            Long id,
            Long outboundProductId,
            Long productGroupImageId,
            String originUrl,
            String externalUrl,
            String imageType,
            int sortOrder,
            boolean deleted,
            Instant deletedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new OutboundProductImageJpaEntity(
                id, outboundProductId, productGroupImageId,
                originUrl, externalUrl, imageType, sortOrder,
                deleted, deletedAt, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getOutboundProductId() {
        return outboundProductId;
    }

    public Long getProductGroupImageId() {
        return productGroupImageId;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public String getImageType() {
        return imageType;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
