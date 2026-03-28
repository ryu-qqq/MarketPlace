package com.ryuqq.marketplace.adapter.out.persistence.order.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** OrderCancel JPA 엔티티. */
@Entity
@Table(name = "order_cancels")
public class OrderCancelJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "cancel_number", nullable = false, length = 50)
    private String cancelNumber;

    @Column(name = "cancel_status", nullable = false, length = 30)
    private String cancelStatus;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "reason_type", length = 50)
    private String reasonType;

    @Column(name = "reason_detail", length = 1000)
    private String reasonDetail;

    @Column(name = "original_amount", nullable = false)
    private int originalAmount;

    @Column(name = "refund_amount", nullable = false)
    private int refundAmount;

    @Column(name = "refund_method", length = 50)
    private String refundMethod;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected OrderCancelJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private OrderCancelJpaEntity(
            Long id,
            String orderId,
            Long orderItemId,
            String cancelNumber,
            String cancelStatus,
            int quantity,
            String reasonType,
            String reasonDetail,
            int originalAmount,
            int refundAmount,
            String refundMethod,
            Instant refundedAt,
            Instant requestedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.cancelNumber = cancelNumber;
        this.cancelStatus = cancelStatus;
        this.quantity = quantity;
        this.reasonType = reasonType;
        this.reasonDetail = reasonDetail;
        this.originalAmount = originalAmount;
        this.refundAmount = refundAmount;
        this.refundMethod = refundMethod;
        this.refundedAt = refundedAt;
        this.requestedAt = requestedAt;
        this.completedAt = completedAt;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static OrderCancelJpaEntity create(
            Long id,
            String orderId,
            Long orderItemId,
            String cancelNumber,
            String cancelStatus,
            int quantity,
            String reasonType,
            String reasonDetail,
            int originalAmount,
            int refundAmount,
            String refundMethod,
            Instant refundedAt,
            Instant requestedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new OrderCancelJpaEntity(
                id,
                orderId,
                orderItemId,
                cancelNumber,
                cancelStatus,
                quantity,
                reasonType,
                reasonDetail,
                originalAmount,
                refundAmount,
                refundMethod,
                refundedAt,
                requestedAt,
                completedAt,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public String getCancelNumber() {
        return cancelNumber;
    }

    public String getCancelStatus() {
        return cancelStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getReasonType() {
        return reasonType;
    }

    public String getReasonDetail() {
        return reasonDetail;
    }

    public int getOriginalAmount() {
        return originalAmount;
    }

    public int getRefundAmount() {
        return refundAmount;
    }

    public String getRefundMethod() {
        return refundMethod;
    }

    public Instant getRefundedAt() {
        return refundedAt;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
