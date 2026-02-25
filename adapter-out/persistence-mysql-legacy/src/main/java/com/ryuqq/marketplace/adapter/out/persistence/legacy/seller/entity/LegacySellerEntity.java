package com.ryuqq.marketplace.adapter.out.persistence.legacy.seller.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacySellerEntity - 레거시 판매자 엔티티.
 *
 * <p>레거시 DB의 seller 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Entity
@Table(name = "seller")
public class LegacySellerEntity extends LegacyBaseEntity {

    @Id
    @Column(name = "seller_id")
    private Long id;

    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "seller_logo_url")
    private String sellerLogoUrl;

    @Column(name = "seller_description")
    private String sellerDescription;

    @Column(name = "commission_rate")
    private Double commissionRate;

    protected LegacySellerEntity() {}

    public Long getId() {
        return id;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getSellerLogoUrl() {
        return sellerLogoUrl;
    }

    public String getSellerDescription() {
        return sellerDescription;
    }

    public Double getCommissionRate() {
        return commissionRate;
    }
}
