package com.ryuqq.marketplace.adapter.out.persistence.refund.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** 환불 대상 상품 JPA 엔티티. */
@Entity
@Table(name = "refund_items")
public class RefundItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "refund_claim_id", nullable = false, length = 36)
    private String refundClaimId;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "refund_qty", nullable = false)
    private int refundQty;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RefundItemJpaEntity() {}

    private RefundItemJpaEntity(
            Long id, String refundClaimId, Long orderItemId, int refundQty, Instant createdAt) {
        this.id = id;
        this.refundClaimId = refundClaimId;
        this.orderItemId = orderItemId;
        this.refundQty = refundQty;
        this.createdAt = createdAt;
    }

    public static RefundItemJpaEntity create(
            Long id, String refundClaimId, Long orderItemId, int refundQty, Instant createdAt) {
        return new RefundItemJpaEntity(id, refundClaimId, orderItemId, refundQty, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getRefundClaimId() {
        return refundClaimId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public int getRefundQty() {
        return refundQty;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
