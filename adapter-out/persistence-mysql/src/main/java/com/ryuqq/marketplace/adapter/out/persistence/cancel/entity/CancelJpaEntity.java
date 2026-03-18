package com.ryuqq.marketplace.adapter.out.persistence.cancel.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * 취소 JPA 엔티티.
 *
 * <p>cancels 테이블과 매핑됩니다. Cancel 1건 = OrderItem 1건의 1회 취소 요청.
 */
@Entity
@Table(name = "cancels")
public class CancelJpaEntity extends BaseAuditEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "cancel_number", nullable = false, length = 50, unique = true)
    private String cancelNumber;

    @Column(name = "order_item_id", nullable = false, length = 36)
    private String orderItemId;

    @Column(name = "seller_id", nullable = false)
    private long sellerId;

    @Column(name = "cancel_qty", nullable = false)
    private int cancelQty;

    @Column(name = "cancel_type", nullable = false, length = 20)
    private String cancelType;

    @Column(name = "cancel_status", nullable = false, length = 20)
    private String cancelStatus;

    @Column(name = "reason_type", nullable = false, length = 50)
    private String reasonType;

    @Column(name = "reason_detail", length = 500)
    private String reasonDetail;

    @Column(name = "refund_amount")
    private Integer refundAmount;

    @Column(name = "refund_method", length = 50)
    private String refundMethod;

    @Column(name = "refund_status", length = 30)
    private String refundStatus;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Column(name = "pg_refund_id", length = 100)
    private String pgRefundId;

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

    /** JPA 스펙 요구사항 - 기본 생성자. */
    protected CancelJpaEntity() {
        super();
    }

    private CancelJpaEntity(
            String id,
            String cancelNumber,
            String orderItemId,
            long sellerId,
            int cancelQty,
            String cancelType,
            String cancelStatus,
            String reasonType,
            String reasonDetail,
            Integer refundAmount,
            String refundMethod,
            String refundStatus,
            Instant refundedAt,
            String pgRefundId,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.cancelNumber = cancelNumber;
        this.orderItemId = orderItemId;
        this.sellerId = sellerId;
        this.cancelQty = cancelQty;
        this.cancelType = cancelType;
        this.cancelStatus = cancelStatus;
        this.reasonType = reasonType;
        this.reasonDetail = reasonDetail;
        this.refundAmount = refundAmount;
        this.refundMethod = refundMethod;
        this.refundStatus = refundStatus;
        this.refundedAt = refundedAt;
        this.pgRefundId = pgRefundId;
        this.requestedBy = requestedBy;
        this.processedBy = processedBy;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.completedAt = completedAt;
    }

    public static CancelJpaEntity create(
            String id,
            String cancelNumber,
            String orderItemId,
            long sellerId,
            int cancelQty,
            String cancelType,
            String cancelStatus,
            String reasonType,
            String reasonDetail,
            Integer refundAmount,
            String refundMethod,
            String refundStatus,
            Instant refundedAt,
            String pgRefundId,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new CancelJpaEntity(
                id,
                cancelNumber,
                orderItemId,
                sellerId,
                cancelQty,
                cancelType,
                cancelStatus,
                reasonType,
                reasonDetail,
                refundAmount,
                refundMethod,
                refundStatus,
                refundedAt,
                pgRefundId,
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

    public String getCancelNumber() {
        return cancelNumber;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public long getSellerId() {
        return sellerId;
    }

    public int getCancelQty() {
        return cancelQty;
    }

    public String getCancelType() {
        return cancelType;
    }

    public String getCancelStatus() {
        return cancelStatus;
    }

    public String getReasonType() {
        return reasonType;
    }

    public String getReasonDetail() {
        return reasonDetail;
    }

    public Integer getRefundAmount() {
        return refundAmount;
    }

    public String getRefundMethod() {
        return refundMethod;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public Instant getRefundedAt() {
        return refundedAt;
    }

    public String getPgRefundId() {
        return pgRefundId;
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
