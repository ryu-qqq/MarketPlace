package com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** ProductGroupImage JPA 엔티티. */
@Entity
@Table(name = "product_group_images")
public class ProductGroupImageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Column(name = "origin_url", nullable = false, length = 500)
    private String originUrl;

    @Column(name = "uploaded_url", length = 500)
    private String uploadedUrl;

    @Column(name = "image_type", nullable = false, length = 50)
    private String imageType;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected ProductGroupImageJpaEntity() {}

    private ProductGroupImageJpaEntity(
            Long id,
            Long productGroupId,
            String originUrl,
            String uploadedUrl,
            String imageType,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.originUrl = originUrl;
        this.uploadedUrl = uploadedUrl;
        this.imageType = imageType;
        this.sortOrder = sortOrder;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public static ProductGroupImageJpaEntity create(
            Long id,
            Long productGroupId,
            String originUrl,
            String uploadedUrl,
            String imageType,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        return new ProductGroupImageJpaEntity(
                id,
                productGroupId,
                originUrl,
                uploadedUrl,
                imageType,
                sortOrder,
                deleted,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getUploadedUrl() {
        return uploadedUrl;
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
