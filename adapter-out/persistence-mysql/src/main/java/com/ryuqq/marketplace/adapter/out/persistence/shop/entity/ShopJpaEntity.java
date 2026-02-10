package com.ryuqq.marketplace.adapter.out.persistence.shop.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Shop JPA 엔티티. */
@Entity
@Table(name = "shop")
public class ShopJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sales_channel_id", nullable = false)
    private Long salesChannelId;

    @Column(name = "shop_name", nullable = false, length = 100)
    private String shopName;

    @Column(name = "account_id", nullable = false, length = 100)
    private String accountId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected ShopJpaEntity() {
        super();
    }

    private ShopJpaEntity(
            Long id,
            Long salesChannelId,
            String shopName,
            String accountId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.shopName = shopName;
        this.accountId = accountId;
        this.status = status;
    }

    public static ShopJpaEntity create(
            Long id,
            Long salesChannelId,
            String shopName,
            String accountId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ShopJpaEntity(
                id, salesChannelId, shopName, accountId, status, createdAt, updatedAt, deletedAt);
    }

    public void update(String shopName, String accountId, String status, Instant updatedAt) {
        this.shopName = shopName;
        this.accountId = accountId;
        this.status = status;
        setUpdatedAt(updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSalesChannelId() {
        return salesChannelId;
    }

    public String getShopName() {
        return shopName;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getStatus() {
        return status;
    }
}
