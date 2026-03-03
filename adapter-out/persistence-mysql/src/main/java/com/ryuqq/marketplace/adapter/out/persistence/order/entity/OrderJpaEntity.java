package com.ryuqq.marketplace.adapter.out.persistence.order.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Order JPA 엔티티. */
@Entity
@Table(name = "orders")
public class OrderJpaEntity extends SoftDeletableEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_number", nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "buyer_name", nullable = false, length = 100)
    private String buyerName;

    @Column(name = "buyer_email", length = 255)
    private String buyerEmail;

    @Column(name = "buyer_phone", length = 20)
    private String buyerPhone;

    @Column(name = "sales_channel_id", nullable = false)
    private long salesChannelId;

    @Column(name = "shop_id", nullable = false)
    private long shopId;

    @Column(name = "external_order_no", nullable = false, length = 100)
    private String externalOrderNo;

    @Column(name = "external_ordered_at", nullable = false)
    private Instant externalOrderedAt;

    protected OrderJpaEntity() {
        super();
    }

    private OrderJpaEntity(
            String id,
            String orderNumber,
            String status,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            long salesChannelId,
            long shopId,
            String externalOrderNo,
            Instant externalOrderedAt,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.buyerPhone = buyerPhone;
        this.salesChannelId = salesChannelId;
        this.shopId = shopId;
        this.externalOrderNo = externalOrderNo;
        this.externalOrderedAt = externalOrderedAt;
    }

    public static OrderJpaEntity create(
            String id,
            String orderNumber,
            String status,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            long salesChannelId,
            long shopId,
            String externalOrderNo,
            Instant externalOrderedAt,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new OrderJpaEntity(
                id,
                orderNumber,
                status,
                buyerName,
                buyerEmail,
                buyerPhone,
                salesChannelId,
                shopId,
                externalOrderNo,
                externalOrderedAt,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public String getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public long getSalesChannelId() {
        return salesChannelId;
    }

    public long getShopId() {
        return shopId;
    }

    public String getExternalOrderNo() {
        return externalOrderNo;
    }

    public Instant getExternalOrderedAt() {
        return externalOrderedAt;
    }
}
