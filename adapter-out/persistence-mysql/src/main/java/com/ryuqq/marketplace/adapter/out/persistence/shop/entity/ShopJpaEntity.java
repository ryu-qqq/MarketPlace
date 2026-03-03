package com.ryuqq.marketplace.adapter.out.persistence.shop.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.converter.EncryptingAttributeConverter;
import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

    @Column(name = "channel_code", length = 50)
    private String channelCode;

    @Convert(converter = EncryptingAttributeConverter.class)
    @Column(name = "api_key", length = 500)
    private String apiKey;

    @Convert(converter = EncryptingAttributeConverter.class)
    @Column(name = "api_secret", length = 500)
    private String apiSecret;

    @Convert(converter = EncryptingAttributeConverter.class)
    @Column(name = "access_token", length = 1000)
    private String accessToken;

    @Column(name = "vendor_id", length = 100)
    private String vendorId;

    protected ShopJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private ShopJpaEntity(
            Long id,
            Long salesChannelId,
            String shopName,
            String accountId,
            String status,
            String channelCode,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.shopName = shopName;
        this.accountId = accountId;
        this.status = status;
        this.channelCode = channelCode;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.accessToken = accessToken;
        this.vendorId = vendorId;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static ShopJpaEntity create(
            Long id,
            Long salesChannelId,
            String shopName,
            String accountId,
            String status,
            String channelCode,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ShopJpaEntity(
                id,
                salesChannelId,
                shopName,
                accountId,
                status,
                channelCode,
                apiKey,
                apiSecret,
                accessToken,
                vendorId,
                createdAt,
                updatedAt,
                deletedAt);
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

    public String getChannelCode() {
        return channelCode;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getVendorId() {
        return vendorId;
    }
}
