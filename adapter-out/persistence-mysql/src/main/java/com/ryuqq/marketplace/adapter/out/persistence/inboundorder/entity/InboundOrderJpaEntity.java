package com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** InboundOrder JPA 엔티티. */
@Entity
@Table(name = "inbound_orders")
public class InboundOrderJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sales_channel_id", nullable = false)
    private long salesChannelId;

    @Column(name = "shop_id", nullable = false)
    private long shopId;

    @Column(name = "seller_id", nullable = false)
    private long sellerId;

    @Column(name = "external_order_no", nullable = false, length = 100)
    private String externalOrderNo;

    @Column(name = "external_ordered_at", nullable = false)
    private Instant externalOrderedAt;

    @Column(name = "buyer_name", nullable = false, length = 100)
    private String buyerName;

    @Column(name = "buyer_email", length = 255)
    private String buyerEmail;

    @Column(name = "buyer_phone", length = 20)
    private String buyerPhone;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "total_payment_amount", nullable = false)
    private int totalPaymentAmount;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private Status status;

    @Column(name = "internal_order_id", length = 36)
    private String internalOrderId;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    protected InboundOrderJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private InboundOrderJpaEntity(
            Long id,
            long salesChannelId,
            long shopId,
            long sellerId,
            String externalOrderNo,
            Instant externalOrderedAt,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            String paymentMethod,
            int totalPaymentAmount,
            Instant paidAt,
            Status status,
            String internalOrderId,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.shopId = shopId;
        this.sellerId = sellerId;
        this.externalOrderNo = externalOrderNo;
        this.externalOrderedAt = externalOrderedAt;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.buyerPhone = buyerPhone;
        this.paymentMethod = paymentMethod;
        this.totalPaymentAmount = totalPaymentAmount;
        this.paidAt = paidAt;
        this.status = status;
        this.internalOrderId = internalOrderId;
        this.failureReason = failureReason;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static InboundOrderJpaEntity create(
            Long id,
            long salesChannelId,
            long shopId,
            long sellerId,
            String externalOrderNo,
            Instant externalOrderedAt,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            String paymentMethod,
            int totalPaymentAmount,
            Instant paidAt,
            Status status,
            String internalOrderId,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundOrderJpaEntity(
                id,
                salesChannelId,
                shopId,
                sellerId,
                externalOrderNo,
                externalOrderedAt,
                buyerName,
                buyerEmail,
                buyerPhone,
                paymentMethod,
                totalPaymentAmount,
                paidAt,
                status,
                internalOrderId,
                failureReason,
                createdAt,
                updatedAt);
    }

    public enum Status {
        RECEIVED,
        MAPPED,
        PENDING_MAPPING,
        CONVERTED,
        FAILED
    }

    public Long getId() {
        return id;
    }

    public long getSalesChannelId() {
        return salesChannelId;
    }

    public long getShopId() {
        return shopId;
    }

    public long getSellerId() {
        return sellerId;
    }

    public String getExternalOrderNo() {
        return externalOrderNo;
    }

    public Instant getExternalOrderedAt() {
        return externalOrderedAt;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int getTotalPaymentAmount() {
        return totalPaymentAmount;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public Status getStatus() {
        return status;
    }

    public String getInternalOrderId() {
        return internalOrderId;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
