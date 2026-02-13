package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * DescriptionImageJpaEntity - 상세설명 이미지 JPA 엔티티.
 *
 * <p>PER-ENT-001: JPA 관계 어노테이션 미사용, Long FK 전략. BaseAuditEntity를 상속하지 않는 독립 자식 엔티티.
 */
@Entity
@Table(name = "description_images")
public class DescriptionImageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_description_id", nullable = false)
    private Long productGroupDescriptionId;

    @Column(name = "origin_url", nullable = false, length = 500)
    private String originUrl;

    @Column(name = "uploaded_url", length = 500)
    private String uploadedUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected DescriptionImageJpaEntity() {}

    private DescriptionImageJpaEntity(
            Long id,
            Long productGroupDescriptionId,
            String originUrl,
            String uploadedUrl,
            int sortOrder) {
        this.id = id;
        this.productGroupDescriptionId = productGroupDescriptionId;
        this.originUrl = originUrl;
        this.uploadedUrl = uploadedUrl;
        this.sortOrder = sortOrder;
    }

    public static DescriptionImageJpaEntity create(
            Long id,
            Long productGroupDescriptionId,
            String originUrl,
            String uploadedUrl,
            int sortOrder) {
        return new DescriptionImageJpaEntity(
                id, productGroupDescriptionId, originUrl, uploadedUrl, sortOrder);
    }

    public Long getId() {
        return id;
    }

    public Long getProductGroupDescriptionId() {
        return productGroupDescriptionId;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getUploadedUrl() {
        return uploadedUrl;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
