package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.converter.EncryptingAttributeConverter;
import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * SellerSalesChannelJpaEntity - 셀러 판매채널 JPA 엔티티.
 *
 * <p>셀러와 외부 판매채널 간 연동 정보를 저장합니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "seller_sales_channels")
public class SellerSalesChannelJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "sales_channel_id", nullable = false)
    private Long salesChannelId;

    @Column(name = "channel_code", nullable = false, length = 50)
    private String channelCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status", nullable = false, length = 20)
    private ConnectionStatus connectionStatus;

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

    @Column(name = "display_name", length = 200)
    private String displayName;

    @Column(name = "shop_id", nullable = false)
    private long shopId;

    protected SellerSalesChannelJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private SellerSalesChannelJpaEntity(
            Long id,
            Long sellerId,
            Long salesChannelId,
            String channelCode,
            ConnectionStatus connectionStatus,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId,
            String displayName,
            long shopId,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.salesChannelId = salesChannelId;
        this.channelCode = channelCode;
        this.connectionStatus = connectionStatus;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.accessToken = accessToken;
        this.vendorId = vendorId;
        this.displayName = displayName;
        this.shopId = shopId;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static SellerSalesChannelJpaEntity create(
            Long id,
            Long sellerId,
            Long salesChannelId,
            String channelCode,
            ConnectionStatus connectionStatus,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId,
            String displayName,
            long shopId,
            Instant createdAt,
            Instant updatedAt) {
        return new SellerSalesChannelJpaEntity(
                id,
                sellerId,
                salesChannelId,
                channelCode,
                connectionStatus,
                apiKey,
                apiSecret,
                accessToken,
                vendorId,
                displayName,
                shopId,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getSalesChannelId() {
        return salesChannelId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
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

    public String getDisplayName() {
        return displayName;
    }

    public long getShopId() {
        return shopId;
    }

    /** 연동 상태. */
    public enum ConnectionStatus {
        CONNECTED,
        DISCONNECTED,
        SUSPENDED
    }
}
