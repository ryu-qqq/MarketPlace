package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** BrandPreset JPA 엔티티. */
@Entity
@Table(name = "brand_preset")
public class BrandPresetJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "sales_channel_brand_id", nullable = false)
    private Long salesChannelBrandId;

    @Column(name = "preset_name", nullable = false, length = 200)
    private String presetName;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected BrandPresetJpaEntity() {
        super();
    }

    private BrandPresetJpaEntity(
            Long id,
            Long shopId,
            Long salesChannelBrandId,
            String presetName,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.shopId = shopId;
        this.salesChannelBrandId = salesChannelBrandId;
        this.presetName = presetName;
        this.status = status;
    }

    public static BrandPresetJpaEntity create(
            Long id,
            Long shopId,
            Long salesChannelBrandId,
            String presetName,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new BrandPresetJpaEntity(
                id, shopId, salesChannelBrandId, presetName, status, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getShopId() {
        return shopId;
    }

    public Long getSalesChannelBrandId() {
        return salesChannelBrandId;
    }

    public String getPresetName() {
        return presetName;
    }

    public String getStatus() {
        return status;
    }
}
