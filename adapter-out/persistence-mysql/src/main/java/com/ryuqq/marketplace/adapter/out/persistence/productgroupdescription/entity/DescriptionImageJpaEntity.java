package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

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

    @Column(name = "origin_url", nullable = false, length = 1000)
    private String originUrl;

    @Column(name = "uploaded_url", length = 1000)
    private String uploadedUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected DescriptionImageJpaEntity() {}

    private DescriptionImageJpaEntity(
            Long id,
            Long productGroupDescriptionId,
            String originUrl,
            String uploadedUrl,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        this.id = id;
        this.productGroupDescriptionId = productGroupDescriptionId;
        this.originUrl = originUrl;
        this.uploadedUrl = uploadedUrl;
        this.sortOrder = sortOrder;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public static DescriptionImageJpaEntity create(
            Long id,
            Long productGroupDescriptionId,
            String originUrl,
            String uploadedUrl,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        return new DescriptionImageJpaEntity(
                id,
                productGroupDescriptionId,
                originUrl,
                uploadedUrl,
                sortOrder,
                deleted,
                deletedAt);
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

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
