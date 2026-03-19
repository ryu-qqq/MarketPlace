package com.ryuqq.marketplace.adapter.out.persistence.ordermapping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * 외부 주문상품 매핑 JPA 엔티티.
 *
 * <p>external_order_item_mappings 테이블과 매핑됩니다. 외부 채널의 externalProductOrderId와 내부 orderItemId 간의 매핑을
 * 저장합니다.
 */
@Entity
@Table(name = "external_order_item_mappings")
public class ExternalOrderItemMappingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sales_channel_id", nullable = false)
    private long salesChannelId;

    @Column(name = "channel_code", nullable = false, length = 20)
    private String channelCode;

    @Column(name = "external_order_id", nullable = false, length = 50)
    private String externalOrderId;

    @Column(name = "external_product_order_id", nullable = false, length = 50)
    private String externalProductOrderId;

    @Column(name = "order_item_id", nullable = false, length = 36)
    private String orderItemId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** JPA 스펙 요구사항 - 기본 생성자. */
    protected ExternalOrderItemMappingJpaEntity() {}

    private ExternalOrderItemMappingJpaEntity(
            Long id,
            long salesChannelId,
            String channelCode,
            String externalOrderId,
            String externalProductOrderId,
            String orderItemId,
            Instant createdAt) {
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.channelCode = channelCode;
        this.externalOrderId = externalOrderId;
        this.externalProductOrderId = externalProductOrderId;
        this.orderItemId = orderItemId;
        this.createdAt = createdAt;
    }

    public static ExternalOrderItemMappingJpaEntity create(
            Long id,
            long salesChannelId,
            String channelCode,
            String externalOrderId,
            String externalProductOrderId,
            String orderItemId,
            Instant createdAt) {
        return new ExternalOrderItemMappingJpaEntity(
                id,
                salesChannelId,
                channelCode,
                externalOrderId,
                externalProductOrderId,
                orderItemId,
                createdAt);
    }

    public Long getId() {
        return id;
    }

    public long getSalesChannelId() {
        return salesChannelId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public String getExternalProductOrderId() {
        return externalProductOrderId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
