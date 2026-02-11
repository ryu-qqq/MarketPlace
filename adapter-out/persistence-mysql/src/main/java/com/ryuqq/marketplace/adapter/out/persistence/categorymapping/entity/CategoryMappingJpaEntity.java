package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** CategoryMapping JPA 엔티티. */
@Entity
@Table(name = "category_mapping")
public class CategoryMappingJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preset_id")
    private Long presetId;

    @Column(name = "sales_channel_category_id", nullable = false)
    private Long salesChannelCategoryId;

    @Column(name = "internal_category_id", nullable = false)
    private Long internalCategoryId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected CategoryMappingJpaEntity() {
        super();
    }

    private CategoryMappingJpaEntity(
            Long id,
            Long presetId,
            Long salesChannelCategoryId,
            Long internalCategoryId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.presetId = presetId;
        this.salesChannelCategoryId = salesChannelCategoryId;
        this.internalCategoryId = internalCategoryId;
        this.status = status;
    }

    public static CategoryMappingJpaEntity create(
            Long id,
            Long presetId,
            Long salesChannelCategoryId,
            Long internalCategoryId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new CategoryMappingJpaEntity(
                id, presetId, salesChannelCategoryId, internalCategoryId, status, createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getPresetId() {
        return presetId;
    }

    public Long getSalesChannelCategoryId() {
        return salesChannelCategoryId;
    }

    public Long getInternalCategoryId() {
        return internalCategoryId;
    }

    public String getStatus() {
        return status;
    }
}
