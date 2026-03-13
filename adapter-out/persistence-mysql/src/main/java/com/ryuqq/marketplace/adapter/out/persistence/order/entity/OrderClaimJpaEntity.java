package com.ryuqq.marketplace.adapter.out.persistence.order.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** OrderClaim JPA 엔티티. 반품/교환 클레임. */
@Entity
@Table(name = "order_claims")
public class OrderClaimJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "order_item_id", nullable = false, length = 36)
    private String orderItemId;

    @Column(name = "claim_number", nullable = false, length = 50)
    private String claimNumber;

    @Column(name = "claim_type", nullable = false, length = 20)
    private String claimType;

    @Column(name = "claim_status", nullable = false, length = 30)
    private String claimStatus;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "reason_type", length = 50)
    private String reasonType;

    @Column(name = "reason_detail", length = 1000)
    private String reasonDetail;

    @Column(name = "collect_method", length = 30)
    private String collectMethod;

    @Column(name = "original_amount", nullable = false)
    private int originalAmount;

    @Column(name = "deduction_amount", nullable = false)
    private int deductionAmount;

    @Column(name = "deduction_reason", length = 500)
    private String deductionReason;

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

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    protected OrderClaimJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private OrderClaimJpaEntity(
            Long id,
            String orderId,
            String orderItemId,
            String claimNumber,
            String claimType,
            String claimStatus,
            int quantity,
            String reasonType,
            String reasonDetail,
            String collectMethod,
            int originalAmount,
            int deductionAmount,
            String deductionReason,
            int refundAmount,
            String refundMethod,
            Instant refundedAt,
            Instant requestedAt,
            Instant completedAt,
            Instant rejectedAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.claimNumber = claimNumber;
        this.claimType = claimType;
        this.claimStatus = claimStatus;
        this.quantity = quantity;
        this.reasonType = reasonType;
        this.reasonDetail = reasonDetail;
        this.collectMethod = collectMethod;
        this.originalAmount = originalAmount;
        this.deductionAmount = deductionAmount;
        this.deductionReason = deductionReason;
        this.refundAmount = refundAmount;
        this.refundMethod = refundMethod;
        this.refundedAt = refundedAt;
        this.requestedAt = requestedAt;
        this.completedAt = completedAt;
        this.rejectedAt = rejectedAt;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static OrderClaimJpaEntity create(
            Long id,
            String orderId,
            String orderItemId,
            String claimNumber,
            String claimType,
            String claimStatus,
            int quantity,
            String reasonType,
            String reasonDetail,
            String collectMethod,
            int originalAmount,
            int deductionAmount,
            String deductionReason,
            int refundAmount,
            String refundMethod,
            Instant refundedAt,
            Instant requestedAt,
            Instant completedAt,
            Instant rejectedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new OrderClaimJpaEntity(
                id,
                orderId,
                orderItemId,
                claimNumber,
                claimType,
                claimStatus,
                quantity,
                reasonType,
                reasonDetail,
                collectMethod,
                originalAmount,
                deductionAmount,
                deductionReason,
                refundAmount,
                refundMethod,
                refundedAt,
                requestedAt,
                completedAt,
                rejectedAt,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public String getClaimType() {
        return claimType;
    }

    public String getClaimStatus() {
        return claimStatus;
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

    public String getCollectMethod() {
        return collectMethod;
    }

    public int getOriginalAmount() {
        return originalAmount;
    }

    public int getDeductionAmount() {
        return deductionAmount;
    }

    public String getDeductionReason() {
        return deductionReason;
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

    public Instant getRejectedAt() {
        return rejectedAt;
    }
}
