package com.ryuqq.marketplace.adapter.out.persistence.refund.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** 환불 클레임 JPA 엔티티. */
@Entity
@Table(name = "refund_claims")
public class RefundClaimJpaEntity extends BaseAuditEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "claim_number", nullable = false, length = 50)
    private String claimNumber;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "seller_id", nullable = false)
    private long sellerId;

    @Column(name = "refund_qty", nullable = false)
    private int refundQty;

    @Column(name = "refund_status", nullable = false, length = 20)
    private String refundStatus;

    @Column(name = "reason_type", nullable = false, length = 50)
    private String reasonType;

    @Column(name = "reason_detail", length = 500)
    private String reasonDetail;

    @Column(name = "original_amount")
    private Integer originalAmount;

    @Column(name = "final_amount")
    private Integer finalAmount;

    @Column(name = "deduction_amount")
    private Integer deductionAmount;

    @Column(name = "deduction_reason", length = 500)
    private String deductionReason;

    @Column(name = "refund_method", length = 50)
    private String refundMethod;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Column(name = "claim_shipment_id", length = 36)
    private String claimShipmentId;

    @Column(name = "hold_reason", length = 500)
    private String holdReason;

    @Column(name = "hold_at")
    private Instant holdAt;

    @Column(name = "requested_by", nullable = false, length = 100)
    private String requestedBy;

    @Column(name = "processed_by", length = 100)
    private String processedBy;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected RefundClaimJpaEntity() {
        super();
    }

    private RefundClaimJpaEntity(
            String id,
            String claimNumber,
            Long orderItemId,
            long sellerId,
            int refundQty,
            String refundStatus,
            String reasonType,
            String reasonDetail,
            Integer originalAmount,
            Integer finalAmount,
            Integer deductionAmount,
            String deductionReason,
            String refundMethod,
            Instant refundedAt,
            String claimShipmentId,
            String holdReason,
            Instant holdAt,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.claimNumber = claimNumber;
        this.orderItemId = orderItemId;
        this.sellerId = sellerId;
        this.refundQty = refundQty;
        this.refundStatus = refundStatus;
        this.reasonType = reasonType;
        this.reasonDetail = reasonDetail;
        this.originalAmount = originalAmount;
        this.finalAmount = finalAmount;
        this.deductionAmount = deductionAmount;
        this.deductionReason = deductionReason;
        this.refundMethod = refundMethod;
        this.refundedAt = refundedAt;
        this.claimShipmentId = claimShipmentId;
        this.holdReason = holdReason;
        this.holdAt = holdAt;
        this.requestedBy = requestedBy;
        this.processedBy = processedBy;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.completedAt = completedAt;
    }

    public static RefundClaimJpaEntity create(
            String id,
            String claimNumber,
            Long orderItemId,
            long sellerId,
            int refundQty,
            String refundStatus,
            String reasonType,
            String reasonDetail,
            Integer originalAmount,
            Integer finalAmount,
            Integer deductionAmount,
            String deductionReason,
            String refundMethod,
            Instant refundedAt,
            String claimShipmentId,
            String holdReason,
            Instant holdAt,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new RefundClaimJpaEntity(
                id,
                claimNumber,
                orderItemId,
                sellerId,
                refundQty,
                refundStatus,
                reasonType,
                reasonDetail,
                originalAmount,
                finalAmount,
                deductionAmount,
                deductionReason,
                refundMethod,
                refundedAt,
                claimShipmentId,
                holdReason,
                holdAt,
                requestedBy,
                processedBy,
                requestedAt,
                processedAt,
                completedAt,
                createdAt,
                updatedAt);
    }

    public String getId() {
        return id;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public long getSellerId() {
        return sellerId;
    }

    public int getRefundQty() {
        return refundQty;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public String getReasonType() {
        return reasonType;
    }

    public String getReasonDetail() {
        return reasonDetail;
    }

    public Integer getOriginalAmount() {
        return originalAmount;
    }

    public Integer getFinalAmount() {
        return finalAmount;
    }

    public Integer getDeductionAmount() {
        return deductionAmount;
    }

    public String getDeductionReason() {
        return deductionReason;
    }

    public String getRefundMethod() {
        return refundMethod;
    }

    public Instant getRefundedAt() {
        return refundedAt;
    }

    public String getClaimShipmentId() {
        return claimShipmentId;
    }

    public String getHoldReason() {
        return holdReason;
    }

    public Instant getHoldAt() {
        return holdAt;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
