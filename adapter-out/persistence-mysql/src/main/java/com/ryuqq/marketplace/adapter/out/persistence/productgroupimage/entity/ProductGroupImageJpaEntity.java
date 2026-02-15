package com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    protected ProductGroupImageJpaEntity() {}

    private ProductGroupImageJpaEntity(
            Long id,
            Long productGroupId,
            String originUrl,
            String uploadedUrl,
            String imageType,
            int sortOrder) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.originUrl = originUrl;
        this.uploadedUrl = uploadedUrl;
        this.imageType = imageType;
        this.sortOrder = sortOrder;
    }

    public static ProductGroupImageJpaEntity create(
            Long id,
            Long productGroupId,
            String originUrl,
            String uploadedUrl,
            String imageType,
            int sortOrder) {
        return new ProductGroupImageJpaEntity(
                id, productGroupId, originUrl, uploadedUrl, imageType, sortOrder);
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
}
