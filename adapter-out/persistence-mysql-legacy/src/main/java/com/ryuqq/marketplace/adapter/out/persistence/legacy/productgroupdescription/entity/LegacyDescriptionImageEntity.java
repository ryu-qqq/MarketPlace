package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * LegacyDescriptionImageEntity - 레거시 상세설명 이미지 JPA 엔티티.
 *
 * <p>레거시 DB의 legacy_description_images 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "legacy_description_images")
public class LegacyDescriptionImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Column(name = "origin_url", nullable = false, length = 500)
    private String originUrl;

    @Column(name = "uploaded_url", length = 500)
    private String uploadedUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected LegacyDescriptionImageEntity() {}

    private LegacyDescriptionImageEntity(
            Long id,
            Long productGroupId,
            String originUrl,
            String uploadedUrl,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.originUrl = originUrl;
        this.uploadedUrl = uploadedUrl;
        this.sortOrder = sortOrder;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public static LegacyDescriptionImageEntity create(
            Long id,
            Long productGroupId,
            String originUrl,
            String uploadedUrl,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        return new LegacyDescriptionImageEntity(
                id, productGroupId, originUrl, uploadedUrl, sortOrder, deleted, deletedAt);
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
