package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** CategoryPreset JPA 엔티티. */
@Entity
@Table(name = "category_preset")
public class CategoryPresetJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "sales_channel_category_id", nullable = false)
    private Long salesChannelCategoryId;

    @Column(name = "preset_name", nullable = false, length = 200)
    private String presetName;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected CategoryPresetJpaEntity() {
        super();
    }

    private CategoryPresetJpaEntity(
            Long id,
            Long shopId,
            Long salesChannelCategoryId,
            String presetName,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.shopId = shopId;
        this.salesChannelCategoryId = salesChannelCategoryId;
        this.presetName = presetName;
        this.status = status;
    }

    public static CategoryPresetJpaEntity create(
            Long id,
            Long shopId,
            Long salesChannelCategoryId,
            String presetName,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new CategoryPresetJpaEntity(
                id, shopId, salesChannelCategoryId, presetName, status, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getShopId() {
        return shopId;
    }

    public Long getSalesChannelCategoryId() {
        return salesChannelCategoryId;
    }

    public String getPresetName() {
        return presetName;
    }

    public String getStatus() {
        return status;
    }
}
